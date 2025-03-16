package edu.uclm.esi.listacompra.http;

import edu.uclm.esi.listacompra.dto.ProductoDTO;
import edu.uclm.esi.listacompra.dto.UsuarioDTO;
import edu.uclm.esi.listacompra.entities.*;
import edu.uclm.esi.listacompra.services.*;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
	private final ProductoService productoService;
	private final UserService userService;
	private final ListaCompraService listaCompraService;

	public ProductoController(ProductoService productoService, UserService userService,
			ListaCompraService listaCompraService) {
		this.productoService = productoService;
		this.userService = userService;
		this.listaCompraService = listaCompraService;
	}

	/**
	 * Obtener los productos de una lista
	 */
	@Deprecated
	@GetMapping("/{listaId}")
	public ResponseEntity<List<ProductoDTO>> obtenerProductos(@PathVariable Integer listaId,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para obtener productos de la lista {}", listaId);
		userService.validarToken(token);
		List<ProductoDTO> productos = productoService.obtenerProductosPorLista(listaId).stream().map(
				p -> new ProductoDTO(p.getId(), p.getNombre(), p.getCantidadTotal(), p.getCantidadComprada(), listaId))
				.collect(Collectors.toList());
		log.info("Lista {} tiene {} productos.", listaId, productos.size());
		return ResponseEntity.ok(productos);
	}
	
	@GetMapping("/{id}/productos")
	public ResponseEntity<Page<ProductoDTO>> obtenerProductosLista(
	    @PathVariable Integer id,
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "10") int size
	) {
	    Page<Producto> productosPage = productoService.obtenerProductosPaginados(id, page, size);
	    
	    Page<ProductoDTO> dtoPage = productosPage.map(p -> 
	        new ProductoDTO(
	            p.getId(),
	            p.getNombre(),
	            p.getCantidadTotal(),
	            p.getCantidadComprada(),
	            id
	        )
	    );
	    
	    return ResponseEntity.ok(dtoPage);
	}

	/**
	 * Agregar un producto a una lista
	 */
	@PostMapping
	public ResponseEntity<ProductoDTO> agregarProducto(@RequestBody @Valid ProductoDTO productoDTO,
			@RequestHeader("Authorization") String token) {
		log.info("Solicitud para agregar producto '{}' a la lista {}", productoDTO.getNombre(), productoDTO.getListaId());
		UsuarioDTO usuario = userService.validarToken(token);

		// Recuperamos la lista desde la BD
		ListaCompra lista = listaCompraService.obtenerListaPorId(productoDTO.getListaId());

		if (!lista.getPropietarioId().equals(usuario.getId()) && !lista.getMiembrosIds().contains(usuario.getId())) {
			log.warn("Usuario {} no tiene permisos para añadir productos a la lista {}", usuario.getId(),
					lista.getId());
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos");
		}

		// Creamos el producto con una instancia real de ListaCompra
		Producto nuevoProducto = new Producto(productoDTO.getNombre(), productoDTO.getCantidadTotal(), 0, usuario.getId(), lista);

		nuevoProducto = productoService.crearProducto(lista, nuevoProducto, usuario.getId());
		log.info("Producto '{}' agregado con ID {} a la lista {}", nuevoProducto.getNombre(), nuevoProducto.getId(),
				productoDTO.getListaId());

		ProductoDTO respuesta = new ProductoDTO(nuevoProducto.getId(), nuevoProducto.getNombre(),
				nuevoProducto.getCantidadTotal(), 0, productoDTO.getListaId());
		return ResponseEntity.status(201).body(respuesta);
	}
}