package edu.uclm.esi.listacompra.services;

import edu.uclm.esi.listacompra.dao.UsuarioDAO;
import edu.uclm.esi.listacompra.entities.Usuario;
import edu.uclm.esi.listacompra.entities.UsuarioValidado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;

@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UsuarioDAO usuarioDAO;

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

	public UserService(WebClient.Builder webClientBuilder, UsuarioDAO usuarioDAO) {
		this.webClient = webClientBuilder.build();
		this.usuarioDAO = usuarioDAO;
	}

	@Cacheable(value = "usuariosCache", key = "#token")
	public UsuarioValidado validarToken(String token) {
		validarFormatoToken(token);
		try {
			log.info("Validando token con backend: " + token);
			UsuarioValidado usuario = webClient.post().uri(backendUsuariosUrl + "/api/auth/validate")
					.bodyValue(new TokenRequest(token)).retrieve().onStatus(status -> status.isError(), response -> {
						log.error("Error HTTP al validar token: " + response.statusCode());
						throw new ResponseStatusException(response.statusCode(), "Error al validar token");
					}).bodyToMono(UsuarioValidado.class).block(Duration.ofSeconds(timeoutSegundos));
			return usuario;
		} catch (Exception e) {
			log.error("Error al validar token: " + e.getMessage());
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido", e);
		}
	}

	private void validarFormatoToken(String token) {
		if (token == null || token.split("\\.").length != 3) {
			log.warn("Token con formato inválido");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de token incorrecto");
		}
	}

	public boolean puedeCrearLista(UsuarioValidado usuario, int listasActuales) {
		return usuario.isPaidUser() || listasActuales < maxListasFree;
	}

	public boolean puedeAñadirMiembro(UsuarioValidado usuario, int miembrosActuales) {
		return usuario.isPaidUser() || miembrosActuales < maxMiembrosFree;
	}

	public boolean puedeAñadirProducto(UsuarioValidado usuario, int productosActuales) {
		return usuario.isPaidUser() || productosActuales < maxProductosFree;
	}
	
	/**
     * Obtiene un usuario por su ID.
     */
    public Usuario obtenerUsuarioPorId(Integer usuarioId) {
        return usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", usuarioId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
                });
    }

	// Clase interna para enviar token
	private record TokenRequest(String token) {
	}
}