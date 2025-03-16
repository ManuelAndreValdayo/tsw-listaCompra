package edu.uclm.esi.listacompra.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "productos")
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
	private int cantidadTotal;
	private int cantidadComprada;

	private Integer creadorId;

	@ManyToOne
	@JoinColumn(name = "lista_compra_id", nullable = false)
	private ListaCompra listaCompra;

	public Producto() {
	}

	public Producto(String nombre, int cantidadTotal, int cantidadComprada, Integer creadoPorId,
			ListaCompra listaCompra) {
		this.nombre = nombre;
		this.cantidadTotal = cantidadTotal;
		this.cantidadComprada = cantidadComprada;
		this.creadorId = creadoPorId;
		this.listaCompra = listaCompra;
	}
	
	@Transient
	public int getCantidadPendiente() {
		return Math.max(0, cantidadTotal - cantidadComprada);
	}

	// Getters y Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCantidadTotal() {
		return cantidadTotal;
	}

	public void setCantidadTotal(int cantidadTotal) {
		this.cantidadTotal = cantidadTotal;
	}

	public int getCantidadComprada() {
		return cantidadComprada;
	}

	public void setCantidadComprada(int cantidadComprada) {
		this.cantidadComprada = cantidadComprada;
	}

	public Integer getCreadorId() {
		return creadorId;
	}

	public void setCreadorId(Integer creadorId) {
		this.creadorId = creadorId;
	}

	public ListaCompra getListaCompra() {
		return listaCompra;
	}

	public void setListaCompra(ListaCompra listaCompra) {
		this.listaCompra = listaCompra;
	}
}
