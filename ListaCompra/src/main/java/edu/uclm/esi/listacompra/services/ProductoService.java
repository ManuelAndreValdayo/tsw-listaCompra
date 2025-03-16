package edu.uclm.esi.listacompra.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.listacompra.dao.ListaCompraDAO;
import edu.uclm.esi.listacompra.dao.ProductoDAO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Service
public class ProductoService {
	private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

	private final ProductoDAO productoDAO;
	private final ListaCompraDAO listaCompraDAO;

	@Value("${app.limites.productos:10}")
	private int maxProductosFree;

	public ProductoService(ProductoDAO productoDAO, ListaCompraDAO listaCompraDAO) {
		this.productoDAO = productoDAO;
		this.listaCompraDAO = listaCompraDAO;
	}

	/**
	 * Crea un nuevo producto en una lista de compra.
	 */
	@Transactional
	public Producto crearProducto(Integer listaId, Producto producto) {
		ListaCompra lista = listaCompraDAO.findById(listaId).orElseThrow(() -> {
			log.warn("Lista de compra no encontrada: {}", listaId);
			return new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista no encontrada");
		});

		producto.setListaCompra(lista);
		Producto nuevoProducto = productoDAO.save(producto);
		log.info("Producto creado: {} en lista {}", nuevoProducto.getNombre(), listaId);
		return nuevoProducto;
	}

	/**
	 * Obtiene todos los productos de una lista de compra.
	 */
	public List<Producto> obtenerProductosPorLista(Integer listaId) {
		return productoDAO.findByListaCompra_Id(listaId);
	}

	/**
	 * Modifica un producto existente (nombre y cantidad total).
	 */
	@Transactional
	public Producto modificarProducto(Integer productoId, Integer listaId, String nuevoNombre, int nuevaCantidad) {
		Producto producto = obtenerProductoPorId(productoId, listaId);
		producto.setNombre(nuevoNombre);
		producto.setCantidadTotal(nuevaCantidad);
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
		producto.setCantidadComprada(producto.getCantidadComprada() + cantidadComprada);
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