package edu.uclm.esi.listacompra.dto;

public class ProductoDTO {
	private Integer id;
	private String nombre;
	private int cantidadTotal;
	private int cantidadComprada;
	private Integer listaId; // Solo el ID de la lista

	public ProductoDTO() {
	}

	public ProductoDTO(Integer id, String nombre, int cantidadTotal, int cantidadComprada, Integer listaId) {
		this.id = id;
		this.nombre = nombre;
		this.cantidadTotal = cantidadTotal;
		this.cantidadComprada = cantidadComprada;
		this.listaId = listaId;
	}

	// Getters y setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getListaId() {
		return listaId;
	}

	public void setListaId(Integer listaId) {
		this.listaId = listaId;
	}
}