package edu.uclm.esi.listacompra.http;

import edu.uclm.esi.listacompra.dto.ProductoDTO;
import edu.uclm.esi.listacompra.dto.UsuarioDTO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;
import edu.uclm.esi.listacompra.services.ListaCompraService;
import edu.uclm.esi.listacompra.services.ProductoService;
import edu.uclm.esi.listacompra.services.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
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
     * Obtener los productos de una lista (método antiguo, mantener por compatibilidad)
     */
    @Deprecated
    @GetMapping("/{listaId}")
    public ResponseEntity<?> obtenerProductos(@PathVariable Integer listaId,
                                              @RequestHeader("Authorization") String token) {
        log.info("Solicitud para obtener productos de la lista {}", listaId);
        userService.validarToken(token);
        var productos = productoService.obtenerProductosPorLista(listaId).stream()
                .map(p -> new ProductoDTO(p.getId(), p.getNombre(), p.getCantidadTotal(),
                        p.getCantidadComprada(), listaId))
                .collect(Collectors.toList());
        log.info("Lista {} tiene {} productos.", listaId, productos.size());
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener productos de una lista de forma paginada
     */
    @GetMapping("/{id}/productos")
    public ResponseEntity<Page<ProductoDTO>> obtenerProductosLista(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Producto> productosPage = productoService.obtenerProductosPaginados(id, page, size);
        Page<ProductoDTO> dtoPage = productosPage.map(p ->
                new ProductoDTO(p.getId(),
                        p.getNombre(),
                        p.getCantidadTotal(),
                        p.getCantidadComprada(),
                        id));
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
            log.warn("Usuario {} no tiene permisos para añadir productos a la lista {}", usuario.getId(), lista.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos");
        }

        // Creamos el producto con una instancia real de ListaCompra
        Producto nuevoProducto = new Producto(productoDTO.getNombre(),
                productoDTO.getCantidadTotal(), 0, usuario.getId(), lista);

        nuevoProducto = productoService.crearProducto(lista, nuevoProducto, usuario.getId());
        log.info("Producto '{}' agregado con ID {} a la lista {}", nuevoProducto.getNombre(),
                nuevoProducto.getId(), productoDTO.getListaId());

        ProductoDTO respuesta = new ProductoDTO(nuevoProducto.getId(), nuevoProducto.getNombre(),
                nuevoProducto.getCantidadTotal(), nuevoProducto.getCantidadComprada(), productoDTO.getListaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Actualizar un producto de forma completa (PUT)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Integer id,
                                                            @RequestBody @Valid ProductoDTO productoDTO,
                                                            @RequestHeader("Authorization") String token) {
        log.info("Solicitud para actualizar el producto con ID {}", id);
        UsuarioDTO usuario = userService.validarToken(token);

        // Se obtiene el producto existente y se valida el permiso
        productoService.obtenerProductoPorId(id, productoDTO.getListaId());
        ListaCompra lista = listaCompraService.obtenerListaPorId(productoDTO.getListaId());
        if (!lista.getPropietarioId().equals(usuario.getId()) && !lista.getMiembrosIds().contains(usuario.getId())) {
            log.warn("Usuario {} no tiene permisos para actualizar el producto {}", usuario.getId(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos");
        }

        // Se delega la actualización completa al service
        Producto actualizado = productoService.modificarProducto(
                id,
                productoDTO.getListaId(),
                productoDTO.getNombre(),
                productoDTO.getCantidadTotal()
        );
        ProductoDTO respuesta = new ProductoDTO(actualizado.getId(), actualizado.getNombre(),
                actualizado.getCantidadTotal(), actualizado.getCantidadComprada(), productoDTO.getListaId());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Actualizar parcialmente un producto (PATCH)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarParcialProducto(@PathVariable Integer id,
                                                                 @RequestBody Map<String, Object> updates,
                                                                 @RequestHeader("Authorization") String token) {
        log.info("Solicitud para actualización parcial del producto con ID {}", id);
        UsuarioDTO usuario = userService.validarToken(token);

        // Se obtiene el producto existente
        // Se asume que el objeto producto tiene asignada la lista en su atributo listaCompra
        Producto productoExistente = productoService.obtenerProductoPorId(id, 
                productoService.obtenerProductoPorId(id, 0).getListaCompra().getId()); // se usa 0 como dummy ya que se sobreentiende

        ListaCompra lista = listaCompraService.obtenerListaPorId(productoExistente.getListaCompra().getId());
        if (!lista.getPropietarioId().equals(usuario.getId()) && !lista.getMiembrosIds().contains(usuario.getId())) {
            log.warn("Usuario {} no tiene permisos para actualizar parcialmente el producto {}", usuario.getId(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos");
        }

        // Se combinan los valores existentes con los nuevos. Solo se actualizan "nombre" y "cantidadTotal"
        String nuevoNombre = productoExistente.getNombre();
        int nuevaCantidadTotal = productoExistente.getCantidadTotal();
        
        if (updates.containsKey("nombre")) {
            nuevoNombre = updates.get("nombre").toString();
        }
        if (updates.containsKey("cantidadTotal")) {
            try {
                nuevaCantidadTotal = Integer.parseInt(updates.get("cantidadTotal").toString());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad total debe ser un número válido");
            }
        }
        // Se delega la actualización parcial al método de modificación completo del service
        Producto actualizado = productoService.modificarProducto(
                id,
                productoExistente.getListaCompra().getId(),
                nuevoNombre,
                nuevaCantidadTotal
        );
        ProductoDTO respuesta = new ProductoDTO(actualizado.getId(), actualizado.getNombre(),
                actualizado.getCantidadTotal(), actualizado.getCantidadComprada(),
                productoExistente.getListaCompra().getId());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Eliminar un producto (DELETE)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id,
                                                   @RequestHeader("Authorization") String token) {
        log.info("Solicitud para eliminar el producto con ID {}", id);
        UsuarioDTO usuario = userService.validarToken(token);

        // Se obtiene el producto existente y se valida el permiso
        Producto productoExistente = productoService.obtenerProductoPorId(id, 
                productoService.obtenerProductoPorId(id, 0).getListaCompra().getId());
        ListaCompra lista = listaCompraService.obtenerListaPorId(productoExistente.getListaCompra().getId());
        if (!lista.getPropietarioId().equals(usuario.getId()) && !lista.getMiembrosIds().contains(usuario.getId())) {
            log.warn("Usuario {} no tiene permisos para eliminar el producto {}", usuario.getId(), id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos");
        }

        productoService.eliminarProducto(id, productoExistente.getListaCompra().getId());
        log.info("Producto con ID {} eliminado", id);
        return ResponseEntity.noContent().build();
    }
}
