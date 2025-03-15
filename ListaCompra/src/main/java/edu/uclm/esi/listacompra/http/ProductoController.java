package edu.uclm.esi.listacompra.http;

import edu.uclm.esi.listacompra.dto.ProductoDTO;
import edu.uclm.esi.listacompra.entities.*;
import edu.uclm.esi.listacompra.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;
    private final UserService userService;
    private final ListaCompraService listaCompraService;

    public ProductoController(ProductoService productoService, UserService userService, ListaCompraService listaCompraService) {
        this.productoService = productoService;
        this.userService = userService;
        this.listaCompraService = listaCompraService;
    }

    /**
     * Obtener los productos de una lista
     */
    @GetMapping("/{listaId}")
    public ResponseEntity<List<ProductoDTO>> obtenerProductos(@PathVariable Integer listaId, @RequestHeader("Authorization") String token) {
        log.info("Solicitud para obtener productos de la lista {}", listaId);
        userService.validarToken(token);
        List<ProductoDTO> productos = productoService.obtenerProductosPorLista(listaId)
                .stream()
                .map(p -> new ProductoDTO(p.getId(), p.getNombre(), p.getCantidadTotal(), p.getCantidadComprada(), listaId))
                .collect(Collectors.toList());
        log.info("Lista {} tiene {} productos.", listaId, productos.size());
        return ResponseEntity.ok(productos);
    }

    /**
     * Agregar un producto a una lista
     */
    @PostMapping
    public ResponseEntity<ProductoDTO> agregarProducto(@RequestBody ProductoDTO productoDTO, @RequestHeader("Authorization") String token) {
        log.info("Solicitud para agregar producto '{}' a la lista {}", productoDTO.getNombre(), productoDTO.getListaId());
        UsuarioValidado usuario = userService.validarToken(token);

        // Recuperamos la lista desde la BD
        ListaCompra lista = listaCompraService.obtenerListaPorId(productoDTO.getListaId());

        // Creamos el producto con una instancia real de ListaCompra
        Producto nuevoProducto = new Producto(
                productoDTO.getNombre(),
                productoDTO.getCantidadTotal(),
                0,  // cantidadComprada inicia en 0
                usuario.getId(), // creadoPorId
                lista // Ahora es una entidad real, no una instancia vacía
        );

        nuevoProducto = productoService.crearProducto(productoDTO.getListaId(), nuevoProducto);
        log.info("Producto '{}' agregado con ID {} a la lista {}", nuevoProducto.getNombre(), nuevoProducto.getId(), productoDTO.getListaId());

        ProductoDTO respuesta = new ProductoDTO(
                nuevoProducto.getId(),
                nuevoProducto.getNombre(),
                nuevoProducto.getCantidadTotal(),
                0,
                productoDTO.getListaId()
        );
        return ResponseEntity.status(201).body(respuesta);
    }
}