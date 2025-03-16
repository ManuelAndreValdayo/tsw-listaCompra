package edu.uclm.esi.listacompra.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.listacompra.dao.ProductoDAO;
import edu.uclm.esi.listacompra.dto.UsuarioDTO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Service
public class ProductoService {
	private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

	private final ProductoDAO productoDAO;
	private final UserService userService;

	@Value("${app.limites.productos:10}")
	private int maxProductosFree;

	public ProductoService(ProductoDAO productoDAO, UserService userService) {
		this.productoDAO = productoDAO;
		this.userService = userService;
	}

	/**
	 * Crea un nuevo producto en una lista de compra.
	 */
	@Transactional
	public Producto crearProducto(ListaCompra lista, Producto producto, Integer usuarioId) {
	    UsuarioDTO usuario = userService.obtenerUsuarioPorId(usuarioId);
	    
	    // Obtener número de productos creados por el usuario en esta lista
	    int productosActuales = productoDAO.countByListaAndCreador(lista.getId(), usuarioId);
	    
	    if (!userService.puedeAñadirProducto(usuario, productosActuales)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Límite de productos alcanzado para usuarios no premium");
	    }

		producto.setListaCompra(lista);
		Producto nuevoProducto = productoDAO.save(producto);
		log.info("Producto creado: {} en lista {}", nuevoProducto.getNombre(), lista.getId());
		return nuevoProducto;
	}

	/**
	 * Obtiene todos los productos de una lista de compra.
	 */
	public List<Producto> obtenerProductosPorLista(Integer listaId) {
		return productoDAO.findByListaCompra_Id(listaId);
	}
	
	public Page<Producto> obtenerProductosPaginados(Integer listaId, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return productoDAO.findByListaCompra_Id(listaId, pageable);
	}

	/**
	 * Modifica un producto existente (nombre y cantidad total).
	 */
	@Transactional
	public Producto modificarProducto(Integer productoId, Integer listaId, String nuevoNombre, int nuevaCantidadTotal) {
		Producto producto = obtenerProductoPorId(productoId, listaId);
		
		if (nuevaCantidadTotal < 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad total no puede ser negativa");
	    }
	    
	    if (nuevaCantidadTotal < producto.getCantidadComprada()) {
	        producto.setCantidadComprada(nuevaCantidadTotal); // Ajustar la cantidad comprada al nuevo total
	    }
	    
		producto.setNombre(nuevoNombre);
		producto.setCantidadTotal(nuevaCantidadTotal);
		Producto actualizado = productoDAO.save(producto);
		log.info("Producto actualizado: {} en lista {}", productoId, listaId);
		return actualizado;
	}

	/**
	 * Elimina un producto de una lista de compra.
	 */
	@Transactional
	public void eliminarProducto(Integer productoId, Integer listaId) {
		if (!productoDAO.existsById(productoId)) {
			log.warn("Intento de eliminar producto inexistente: {}", productoId);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
		}
		productoDAO.deleteByIdAndListaCompra_Id(productoId, listaId);
		log.info("Producto eliminado: {} en lista {}", productoId, listaId);
	}

	/**
	 * Registra una compra en un producto, aumentando la cantidad comprada.
	 */
	@Transactional
	public Producto registrarCompra(Integer productoId, Integer listaId, int cantidadComprada) {
		Producto producto = obtenerProductoPorId(productoId, listaId);
		if (cantidadComprada < 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad no puede ser negativa");
	    }
		int nuevaCantidad = producto.getCantidadComprada() + cantidadComprada;
	    nuevaCantidad = Math.min(nuevaCantidad, producto.getCantidadTotal()); // Ajustar al máximo permitido
	    
		producto.setCantidadComprada(nuevaCantidad);
		Producto actualizado = productoDAO.save(producto);
		log.info("Se registró compra de {} unidades en producto {} en lista {}", cantidadComprada, productoId, listaId);
		return actualizado;
	}

	/**
	 * Obtiene un producto por ID dentro de una lista.
	 */
	public Producto obtenerProductoPorId(Integer productoId, Integer listaId) {
		return productoDAO.findById(productoId).filter(p -> p.getListaCompra().getId().equals(listaId))
				.orElseThrow(() -> {
					log.warn("Producto no encontrado: {} en lista {}", productoId, listaId);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado en la lista");
				});
	}
}