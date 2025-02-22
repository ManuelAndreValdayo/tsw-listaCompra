package edu.uclm.esi.listacompra.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import edu.uclm.esi.listacompra.dao.ListaCompraDAO;
import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Service
public class ListaCompraService {
	@Autowired
	private ListaCompraDAO listaCompraDAO;
	
	public ResponseEntity<Integer> create(ListaCompra lista){
		ListaCompra saveLista = this.listaCompraDAO.save(lista);
		return ResponseEntity.status(HttpStatus.CREATED).body(saveLista.getId());	
	}
	public ResponseEntity<Optional<List<ListaCompra>>> getAllListasById(Integer IdUsuario){
		Optional<List<ListaCompra>> todasListas = this.listaCompraDAO.findByUserId(IdUsuario);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(todasListas);
	}
	public ResponseEntity<Optional<ListaCompra>> getListaById(Integer IdLista){
		Optional<ListaCompra> lista = this.listaCompraDAO.findById(IdLista);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(lista);
	}
	public ResponseEntity<Integer> modificar(ListaCompra lista){
		Integer Id = this.listaCompraDAO.updateLista(lista);
		return ResponseEntity.status(HttpStatus.CREATED).body(Id);	
	}
	
	public boolean eliminar(int IdLista){
		boolean v = false;
		if(this.listaCompraDAO.findById(IdLista).isPresent()) {
			this.listaCompraDAO.deleteById(IdLista);
			v = true;
		}
		return v;
	}
}
