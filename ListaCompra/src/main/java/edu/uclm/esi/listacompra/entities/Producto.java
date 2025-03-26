package edu.uclm.esi.listacompra.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
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
}
