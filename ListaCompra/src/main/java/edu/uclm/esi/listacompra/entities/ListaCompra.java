package edu.uclm.esi.listacompra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "ListaCompra")
public class ListaCompra {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
	@JsonProperty("Nombre")
	@Column(name = "nombre")
	private String Nombre;
	
	@JsonProperty("id_usuario")
	@Column(name = "id_usuario")
	private int id_usuario;
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public int getIdUsuario() {
		return id_usuario;
	}
	public void setIdUsuario(int idUsuario) {
		id_usuario = idUsuario;
	}
}
