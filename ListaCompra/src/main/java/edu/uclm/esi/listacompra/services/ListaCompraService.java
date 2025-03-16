package edu.uclm.esi.listacompra.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.listacompra.dao.ListaCompraDAO;
import edu.uclm.esi.listacompra.dto.ListaCompraDetalleDTO;
import edu.uclm.esi.listacompra.dto.ProductoDTO;
import edu.uclm.esi.listacompra.dto.UsuarioDTO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Service
public class ListaCompraService {
	private static final Logger log = LoggerFactory.getLogger(ListaCompraService.class);

	private final ListaCompraDAO listaCompraDAO;
	private final UserService userService;
	private final ProductoService productoService;

	@Value("${app.limites.listas:2}")
	private int maxListasFree;

	public ListaCompraService(ListaCompraDAO listaCompraDAO, UserService userService, ProductoService productoService) {
	    this.listaCompraDAO = listaCompraDAO;
	    this.userService = userService;
	    this.productoService = productoService;
	}
	
	public ListaCompraDetalleDTO convertirADetalleDTO(ListaCompra lista, Pageable pageable) {
	    Page<Producto> productosPage = productoService.obtenerProductosPaginados(lista.getId(), pageable.getPageNumber(), pageable.getPageSize());

	    UsuarioDTO propietario = userService.obtenerUsuarioPorId(lista.getPropietarioId());
	    List<UsuarioDTO> miembros = userService.obtenerUsuariosPorIds(lista.getMiembrosIds());
	    Page<ProductoDTO> productosDTO = productosPage.map(p -> 
	        new ProductoDTO(p.getId(), p.getNombre(), p.getCantidadTotal(), p.getCantidadComprada(), lista.getId())
	    );
	    
	    return new ListaCompraDetalleDTO(
	        lista.getId(),
	        lista.getNombre(),
	        propietario,
	        miembros,
	        productosDTO
	    );
	}

	/**
	 * Crea una nueva lista de compra con un propietario.
	 */
	@Transactional
	public ListaCompra crearLista(String nombre, Integer propietarioId) {
		UsuarioDTO propietario = userService.obtenerUsuarioPorId(propietarioId);
	    if (propietario == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se debe proporcionar un usuario como propietario");
	    }
	    
	    // Validar límite de listas para usuarios no premium
	    if (!propietario.isPremium()) {
	        int listasActuales = listaCompraDAO.findByPropietario_Id(propietarioId).size();
	        if (!userService.puedeCrearLista(propietario, listasActuales)) {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Límite de listas alcanzado");
	        }
	    }
	    
		ListaCompra lista = new ListaCompra(nombre, propietarioId);
		ListaCompra nuevaLista = listaCompraDAO.save(lista);
		log.info("Lista creada: {} por usuario {}", nuevaLista.getNombre(), propietarioId);
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
	public ListaCompra modificarLista(Integer listaId, String nuevoNombre, Integer propietarioId) {
		ListaCompra lista = obtenerListaPorId(listaId);
		if (!lista.getPropietarioId().equals(propietarioId)) {
			log.warn("Usuario {} intentó modificar la lista {} sin permiso", propietarioId, listaId);
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar esta lista");
		}
		lista.setNombre(nuevoNombre);
		return listaCompraDAO.save(lista);
	}

	/**
	 * Elimina una lista de compra (solo el propietario puede hacerlo).
	 */
	@Transactional
	public void eliminarLista(Integer listaId, Integer propietarioId) {
		ListaCompra lista = obtenerListaPorId(listaId);
		if (!lista.getPropietarioId().equals(propietarioId)) {
			log.warn("Usuario {} intentó eliminar la lista {} sin permiso", propietarioId, listaId);
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
	
	/**
	 * Añadir el id de un usuario a la lista de miembros
	 */
	@Transactional
	public void agregarMiembro(Integer listaId, Integer usuarioId, Integer nuevoMiembroId) {
	    ListaCompra lista = obtenerListaPorId(listaId);
	    UsuarioDTO usuario = userService.obtenerUsuarioPorId(usuarioId);

	    if (!userService.puedeAñadirMiembro(usuario, lista.getMiembrosIds().size())) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Límite de miembros alcanzado");
	    }

	    lista.getMiembrosIds().add(nuevoMiembroId);
	    listaCompraDAO.save(lista);
	}
	
	@Transactional
	public void eliminarMiembro(Integer listaId, Integer propietarioId, Integer miembroId) {
	    ListaCompra lista = obtenerListaPorId(listaId);
	    
	    // Validar permisos (solo el propietario puede eliminar miembros)
	    if (!lista.getPropietarioId().equals(propietarioId)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el propietario puede eliminar miembros");
	    }
	    
	    if (!lista.getMiembrosIds().contains(miembroId)) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El miembro no existe en la lista");
	    }
	    
	    lista.getMiembrosIds().remove(miembroId);
	    listaCompraDAO.save(lista);
	}
}