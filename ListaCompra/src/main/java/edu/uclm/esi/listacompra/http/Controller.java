package edu.uclm.esi.listacompra.http;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.listacompra.entities.Credenciales;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;
import edu.uclm.esi.listacompra.services.ListaCompraService;
import edu.uclm.esi.listacompra.services.ProductoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = { "https://localhost:4200" }, allowCredentials = "true")
@RestController
@RequestMapping("/listaCompra")
public class Controller {

	@Autowired ProductoService productoService;
	@Autowired ListaCompraService listaCompraService;
	
	@PostMapping("/anadirProducto")
	public ResponseEntity<Integer> registrarProducto(@RequestBody Producto producto){
		return productoService.create(producto);
	}
	@PutMapping("/modificarProducto")
	public ResponseEntity<Integer> modificarLista(HttpServletRequest request,HttpSession session, @RequestBody(required = false) Producto producto){
		return productoService.modificarProducto(producto);
	}
	@PostMapping("/crearLista")
	public ResponseEntity<Integer> crearLista(HttpServletRequest request,HttpSession session, @RequestBody(required = false) ListaCompra listaCompra){
        listaCompra.setIdUsuario(obtenerUserId(request));
		return listaCompraService.create(listaCompra);
	}
	@PutMapping("/modificarLista")
	public ResponseEntity<Integer> modificarLista(HttpServletRequest request,HttpSession session, @RequestBody(required = false) ListaCompra listaCompra){
		System.out.println(listaCompra.getId());
		return listaCompraService.modificar(listaCompra);
	}
	@GetMapping("/getAllUserListas")
	public ResponseEntity<Optional<List<ListaCompra>>> getAllUserListas(HttpServletRequest request) {
		return this.listaCompraService.getAllListasById(obtenerUserId(request));
	} 
	@GetMapping("/getAllProductos/{idLista}")
	public ResponseEntity<Optional<List<Producto>>> getAllProductos(@PathVariable Integer idLista) {
		return this.productoService.getAllProductosByIdLista(idLista);
	} 
	@GetMapping("/getProducto/{id}")
	public ResponseEntity<Optional<Producto>> getProducto(@PathVariable Integer id) {
		return this.productoService.getProductoById(id);
	} 
	@GetMapping("/getLista/{id}")
	public ResponseEntity<Optional<ListaCompra>> getLista(@PathVariable Integer id) {
		return this.listaCompraService.getListaById(id);
	} 
	@DeleteMapping("/eliminarLista/{id}")
	public boolean eliminarLista(@PathVariable Integer id){
		return this.listaCompraService.eliminar(id);
	}
	@DeleteMapping("/eliminarProducto/{id}")
	public boolean eliminarProducto(@PathVariable Integer id){
		return this.productoService.eliminar(id);
	}
	@GetMapping("/getAllProducts")
	public ResponseEntity<List<Producto>> getAllProducts() {
		return this.productoService.getAll();
	} 
	
	@DeleteMapping("/eliminarProducto")
	public boolean eliminarProducto(int IdProducto){
		return this.productoService.eliminar(IdProducto);
	}
	private String findCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null)
			return null;
		for (int i=0; i<cookies.length; i++)
			if (cookies[i].getName().equals(cookieName))
				return cookies[i].getValue();
		return null;
	}

	private Integer obtenerUserId(HttpServletRequest request) {
		String cookie = this.findCookie(request, "dwd23e39fjpdwm3");
        byte[] decodedBytes = Base64.getDecoder().decode(cookie);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
		return Integer.parseInt(decodedString);
	}
}
