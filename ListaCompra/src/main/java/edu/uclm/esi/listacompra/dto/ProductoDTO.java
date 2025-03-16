package edu.uclm.esi.listacompra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductoDTO {
	private Integer id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @PositiveOrZero(message = "La cantidad total debe ser cero o positiva")
    private int cantidadTotal;

    @PositiveOrZero(message = "La cantidad comprada debe ser cero o positiva")
    private int cantidadComprada;

    @NotNull(message = "El ID de la lista es obligatorio")
    @Positive(message = "El ID de la lista debe ser un número válido")
    private Integer listaId;

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