package edu.uclm.esi.listacompra.services;

import edu.uclm.esi.listacompra.dto.UsuarioDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.util.List;

@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final WebClient webClient;

	@Value("${backend.usuarios.url}")
	private String backendUsuariosUrl;

	@Value("${app.timeout.segundos:5}")
	private int timeoutSegundos;

	@Value("${app.limites.listas:2}")
	private int maxListasFree;

	@Value("${app.limites.miembros:1}")
	private int maxMiembrosFree;

	@Value("${app.limites.productos:10}")
	private int maxProductosFree;

	public UserService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}

	@Cacheable(value = "usuariosCache", key = "#token")
	public UsuarioDTO validarToken(String token) {
		validarFormatoToken(token);
		try {
			log.info("Validando token con backend: " + token);
			UsuarioDTO usuario = webClient.post().uri(backendUsuariosUrl + "/api/auth/validate")
					.bodyValue(new TokenRequest(token)).retrieve().onStatus(status -> status.isError(), response -> {
						log.error("Error HTTP al validar token: " + response.statusCode());
						throw new ResponseStatusException(response.statusCode(), "Error al validar token");
					}).bodyToMono(UsuarioDTO.class).block(Duration.ofSeconds(timeoutSegundos));
			
			log.info("Token válido. Usuario ID: {}", usuario.getId());
			return usuario;
		} catch (Exception e) {
			// Verificar si la causa es TimeoutException
	        Throwable rootCause = e.getCause();
	        if (rootCause instanceof java.util.concurrent.TimeoutException) {
	            log.error("Timeout al validar el token: {}", rootCause.getMessage());
	            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Tiempo de espera agotado", e);
	        } else {
	            log.error("Error al validar token: {}", e.getMessage());
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido", e);
	        }
		}
	}

	private void validarFormatoToken(String token) {
		if (token == null || token.split("\\.").length != 3) {
			log.warn("Token con formato inválido");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de token incorrecto");
		}
	}

	public boolean puedeCrearLista(UsuarioDTO usuario, int listasActuales) {
		return usuario.isPremium() || listasActuales < maxListasFree;
	}

	public boolean puedeAñadirMiembro(UsuarioDTO usuario, int miembrosActuales) {
		return usuario.isPremium() || miembrosActuales < maxMiembrosFree;
	}

	public boolean puedeAñadirProducto(UsuarioDTO usuario, int productosActuales) {
		return usuario.isPremium() || productosActuales < maxProductosFree;
	}
	
	/**
	 * 
	 * @param ids
	 * @return List<UsuarioDTO>
	 */
	public List<UsuarioDTO> obtenerUsuariosPorIds(List<Integer> ids) {
	    try {
	        return webClient.post()
	            .uri(backendUsuariosUrl + "/api/usuarios/batch")
	            .bodyValue(ids)
	            .retrieve()
	            .onStatus(status -> status.isError(), response -> {
	                log.error("Error al obtener usuarios: {}", response.statusCode());
	                throw new ResponseStatusException(response.statusCode(), "Error en backend de usuarios");
	            })
	            .bodyToMono(new ParameterizedTypeReference<List<UsuarioDTO>>() {})
	            .block(Duration.ofSeconds(timeoutSegundos));
	    } catch (Exception e) {
	        log.error("Error al obtener usuarios: {}", e.getMessage());
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al recuperar usuarios");
	    }
	}

	/**
	 * Obtiene un usuario por su ID.
	 */
	@Cacheable(value = "usuariosCache", key = "#usuarioId")
	public UsuarioDTO obtenerUsuarioPorId(Integer usuarioId) {
	    try {
	        log.info("Obteniendo datos del usuario con ID {}", usuarioId);
	        return webClient.get()
	                .uri(backendUsuariosUrl + "/api/usuarios/" + usuarioId)
	                .retrieve()
	                .bodyToMono(UsuarioDTO.class)
	                .block(Duration.ofSeconds(timeoutSegundos));
	    } catch (Exception e) {
	        log.error("Error al obtener usuario con ID {}", usuarioId, e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al recuperar usuario");
	    }
	}

	// Clase interna para enviar token
	private record TokenRequest(String token) {
	}

	public String obtenerNombreUsuario(Integer usuarioId) {
	    UsuarioDTO usuario = obtenerUsuarioPorId(usuarioId);
	    return (usuario != null) ? usuario.getNombre() : "Usuario Desconocido";
	}
}