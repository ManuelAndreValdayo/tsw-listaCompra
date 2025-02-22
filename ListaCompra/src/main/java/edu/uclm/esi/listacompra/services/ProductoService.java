package edu.uclm.esi.listacompra.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import edu.uclm.esi.listacompra.dao.ListaCompraDAO;
import edu.uclm.esi.listacompra.dao.ProductoDAO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Service
public class ProductoService {
	
	@Autowired
	private ProductoDAO productoDAO;
	
	@Autowired
	private ListaCompraDAO listaCompraDAO;
	
	public ResponseEntity<Integer> create(Producto producto){
		if(this.comprobarProducto(producto)) {
			Producto saveProducto = this.productoDAO.save(producto);
			return ResponseEntity.status(HttpStatus.CREATED).body(saveProducto.getId());			
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}
	public ResponseEntity<Integer> modificarProducto(Producto producto){
		if(this.comprobarProductoNombre(producto)) {
			return ResponseEntity.status(HttpStatus.CREATED).body(this.productoDAO.modificar(producto));			
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		
	}
	public ResponseEntity<Integer> buscarProducto(String nombre){
		Optional <Integer> Id = this.productoDAO.findProductByName(nombre);
		return Id.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	public ResponseEntity<Optional<List<Producto>>> getAllProductosByIdLista(Integer IdLista){
		Optional<List<Producto>> productos = this.productoDAO.findProductByIdLista(IdLista);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(productos);
	}
	public ResponseEntity <List<Producto>> getAll(){
		List<Producto> ListaProductos = this.productoDAO.findAll();
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(ListaProductos);
	}
	public ResponseEntity<Optional<Producto>> getProductoById(Integer IdProducto){
		Optional<Producto> lista = this.productoDAO.findById(IdProducto);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(lista);
	}
	public boolean eliminar(int IdProducto){
		boolean v = false;
		if(this.productoDAO.findById(IdProducto).isPresent()) {
			this.productoDAO.deleteById(IdProducto);
			v = true;
		}
		return v;
	}
	public boolean comprobarProducto(Producto producto) {
		boolean b = false;
		if(!producto.getNombre().equals("") && producto.getIdListaCompra()>0 && producto.getCantidad()>0) {
			if(this.productoDAO.findProductByIdLista(producto.getNombre(), producto.getIdListaCompra()).isEmpty() && this.listaCompraDAO.findById(producto.getIdListaCompra()).isPresent()) {
				b = true;				
			}
		}
		return b;
	}
	public boolean comprobarProductoNombre(Producto producto) {
		boolean b = false;
		if(this.productoDAO.findProductByIdLista(producto.getNombre(), producto.getIdListaCompra()).isEmpty()) {
			b = true;
		}
		return b;
	}
	
}
