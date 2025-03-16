package edu.uclm.esi.listacompra.http;

import edu.uclm.esi.listacompra.dto.ListaCompraDTO;
import edu.uclm.esi.listacompra.dto.ProductoDTO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Usuario;
import edu.uclm.esi.listacompra.services.ListaCompraService;
import edu.uclm.esi.listacompra.services.UserService;
import edu.uclm.esi.listacompra.entities.UsuarioValidado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listas")
public class ListaCompraController {
	private static final Logger log = LoggerFactory.getLogger(ListaCompraController.class);
	private final ListaCompraService listaCompraService;
	private final UserService userService;

	public ListaCompraController(ListaCompraService listaCompraService, UserService userService) {
		this.listaCompraService = listaCompraService;
		this.userService = userService;
	}

	/**
	 * Obtener todas las listas de un usuario autenticado
	 */
	@GetMapping
	public ResponseEntity<List<ListaCompraDTO>> obtenerListas(@RequestHeader("Authorization") String token) {
		log.info("Solicitud para obtener listas de compra con token: {}", token);
		UsuarioValidado usuario = userService.validarToken(token);
		List<ListaCompraDTO> listas = listaCompraService.obtenerListasPorUsuario(usuario.getId()).stream()
				.map(lista -> new ListaCompraDTO(lista.getId(), lista.getNombre(), lista.getPropietario().getId(),
						lista.getProductos().stream()
								.map(p -> new ProductoDTO(p.getId(), p.getNombre(), p.getCantidadTotal(),
										p.getCantidadComprada(), lista.getId()))
								.collect(Collectors.toList())))
				.collect(Collectors.toList());
		log.info("Usuario {} tiene {} listas de compra.", usuario.getId(), listas.size());
		return ResponseEntity.ok(listas);
	}

	/**
	 * Crear una nueva lista de compra
	 */
	@PostMapping
	public ResponseEntity<ListaCompraDTO> crearLista(@RequestBody ListaCompraDTO listaDTO,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para crear lista: {}", listaDTO.getNombre());
		UsuarioValidado usuarioValidado = userService.validarToken(token);
		Usuario usuario = userService.obtenerUsuarioPorId(usuarioValidado.getId());

		ListaCompra nuevaLista = new ListaCompra(listaDTO.getNombre(), usuario);
		nuevaLista = listaCompraService.crearLista(listaDTO.getNombre(), usuario);
		log.info("Lista creada con ID {} por usuario {}", nuevaLista.getId(), usuario.getId());

		ListaCompraDTO respuesta = new ListaCompraDTO(nuevaLista.getId(), nuevaLista.getNombre(), usuario.getId(),
				null);
		return ResponseEntity.status(201).body(respuesta);
	}
}