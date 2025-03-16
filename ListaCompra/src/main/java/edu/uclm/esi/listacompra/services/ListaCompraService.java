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
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Usuario;

@Service
public class ListaCompraService {
	private static final Logger log = LoggerFactory.getLogger(ListaCompraService.class);

	private final ListaCompraDAO listaCompraDAO;

	@Value("${app.limites.listas:2}")
	private int maxListasFree;

	public ListaCompraService(ListaCompraDAO listaCompraDAO) {
		this.listaCompraDAO = listaCompraDAO;
	}

	/**
	 * Crea una nueva lista de compra con un propietario.
	 */
	@Transactional
	public ListaCompra crearLista(String nombre, Usuario propietario) {
		ListaCompra lista = new ListaCompra(nombre, propietario);
		ListaCompra nuevaLista = listaCompraDAO.save(lista);
		log.info("Lista creada: {} por usuario {}", nuevaLista.getNombre(), propietario.getId());
		return nuevaLista;
	}

	/**
	 * Obtiene todas las listas donde un usuario es propietario o miembro.
	 */
	public List<ListaCompra> obtenerListasPorUsuario(Integer usuarioId) {
		return listaCompraDAO.findByPropietarioOrMiembro(usuarioId);
	}

	/**
	 * Modifica el nombre de una lista (solo el propietario puede hacerlo).
	 */
	@Transactional
	public ListaCompra modificarLista(Integer listaId, String nuevoNombre, Usuario propietario) {
		ListaCompra lista = obtenerListaPorId(listaId);
		if (!lista.getPropietario().getId().equals(propietario.getId())) {
			log.warn("Usuario {} intentó modificar la lista {} sin permiso", propietario.getId(), listaId);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar esta lista");
		}
		lista.setNombre(nuevoNombre);
		return listaCompraDAO.save(lista);
	}

	/**
	 * Elimina una lista de compra (solo el propietario puede hacerlo).
	 */
	@Transactional
	public void eliminarLista(Integer listaId, Usuario propietario) {
		ListaCompra lista = obtenerListaPorId(listaId);
		if (!lista.getPropietario().getId().equals(propietario.getId())) {
			log.warn("Usuario {} intentó eliminar la lista {} sin permiso", propietario.getId(), listaId);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para eliminar esta lista");
		}
		listaCompraDAO.delete(lista);
		log.info("Lista eliminada: {}", listaId);
	}

	/**
	 * Obtiene una lista de compra por su ID.
	 */
	public ListaCompra obtenerListaPorId(Integer listaId) {
		return listaCompraDAO.findById(listaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista no encontrada"));
	}
}