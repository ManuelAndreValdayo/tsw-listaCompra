package edu.uclm.esi.listacompra.http;

import edu.uclm.esi.listacompra.dto.ListaCompraDetalleDTO;
import edu.uclm.esi.listacompra.dto.ListaCompraResumenDTO;
import edu.uclm.esi.listacompra.dto.UsuarioDTO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.services.ListaCompraService;
import edu.uclm.esi.listacompra.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
	public ResponseEntity<List<ListaCompraResumenDTO>> obtenerListasResumen(
			@RequestHeader("Authorization") String token) {

		UsuarioDTO usuario = userService.validarToken(token);
		log.info("Solicitud de {} para obtener listas de compra", usuario.getNombre());

		// Obtener todas las listas del usuario
		List<ListaCompra> listas = listaCompraService.obtenerListasPorUsuario(usuario.getId());

		List<ListaCompraResumenDTO> resumen = listas.stream()
				.map(lista -> new ListaCompraResumenDTO(lista.getId(), lista.getNombre(),
						userService.obtenerNombreUsuario(lista.getPropietarioId()), // Optimizado: Solo nombre
						lista.getMiembrosIds().size(), lista.getProductos().size()))
				.collect(Collectors.toList());

		log.info("Usuario {} tiene {} listas de compra.", usuario.getId(), listas.size());
		return ResponseEntity.ok(resumen);
	}

	/**
	 * Obtener detalle de una lista
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ListaCompraDetalleDTO> obtenerListaDetalle(
	    @RequestHeader("Authorization") String token,
	    @PathVariable Integer id,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size
	) {
	    UsuarioDTO usuario = userService.validarToken(token);
	    log.info("Solicitud de {} para obtener detalle de lista {}", usuario.getNombre(), id);
	    
	    Pageable pageable = PageRequest.of(page, size); // Importar de org.springframework.data.domain
	    ListaCompra lista = listaCompraService.obtenerListaPorId(id);
	    ListaCompraDetalleDTO dto = listaCompraService.convertirADetalleDTO(lista, pageable);
	    
	    return ResponseEntity.ok(dto);
	}

	/**
	 * Crear una nueva lista de compra
	 */
	@PostMapping
	public ResponseEntity<ListaCompraDetalleDTO> crearLista(@RequestBody String nombreLista,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para crear lista: {}", nombreLista);

		UsuarioDTO usuario = userService.validarToken(token);

		// Crear lista directamente desde el servicio
		ListaCompra nuevaLista = listaCompraService.crearLista(nombreLista, usuario.getId());

		log.info("Lista creada con ID {} por usuario {}", nuevaLista.getId(), usuario.getId());

		// Construir la respuesta con la información del propietario
		ListaCompraDetalleDTO respuesta = new ListaCompraDetalleDTO(nuevaLista.getId(), nuevaLista.getNombre(), usuario,
				null, // Miembros vacíos en la creación
				null // Productos vacíos en la creación
		);

		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarLista(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
		log.info("Solicitud para eliminar lista con ID {}", id);

		UsuarioDTO usuario = userService.validarToken(token);

		listaCompraService.eliminarLista(id, usuario.getId());
		log.info("Lista {} eliminada por usuario {}", id, usuario.getId());

		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<ListaCompraDetalleDTO> editarLista(@PathVariable Integer id,
			@RequestBody ListaCompraResumenDTO listaDTO, @RequestHeader("Authorization") String token) {
		log.info("Solicitud para actualizar la lista {}", id);

		UsuarioDTO usuario = userService.validarToken(token);
		ListaCompra listaActualizada = listaCompraService.modificarLista(id, listaDTO.getNombre(), usuario.getId());

		ListaCompraDetalleDTO respuesta = new ListaCompraDetalleDTO(listaActualizada.getId(),
				listaActualizada.getNombre(), usuario, null, // No actualizamos miembros aquí
				null // No actualizamos productos aquí
		);

		return ResponseEntity.ok(respuesta);
	}

	@PostMapping("/{id}/miembros")
	public ResponseEntity<Void> agregarMiembro(@PathVariable Integer id, @RequestBody UsuarioDTO nuevoMiembro,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para agregar usuario {} a la lista {}", nuevoMiembro.getId(), id);

		UsuarioDTO usuario = userService.validarToken(token);
		ListaCompra lista = listaCompraService.obtenerListaPorId(id);
		
		// Validar si el usuario es propietario
	    if (!lista.getPropietarioId().equals(usuario.getId())) {
	        log.warn("Usuario {} intentó añadir miembros sin permiso", usuario.getId());
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el propietario puede añadir miembros");
	    }
	    
		listaCompraService.agregarMiembro(id, usuario.getId(), nuevoMiembro.getId());

		log.info("Usuario {} agregado a la lista {}", nuevoMiembro.getId(), id);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{id}/miembros/{usuarioId}")
	public ResponseEntity<Void> eliminarMiembro(@PathVariable Integer id, @PathVariable Integer usuarioId,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para eliminar usuario {} de la lista {}", usuarioId, id);

		UsuarioDTO usuario = userService.validarToken(token);
		listaCompraService.eliminarMiembro(id, usuario.getId(), usuarioId);

		log.info("Usuario {} eliminado de la lista {}", usuarioId, id);
		return ResponseEntity.noContent().build();
	}
}