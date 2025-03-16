package edu.uclm.esi.listacompra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ListaCompraResumenDTO {
	private Integer id;

    @NotBlank(message = "El nombre de la lista no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El nombre del propietario no puede estar vacío")
    private String propietarioNombre;

    @PositiveOrZero(message = "El número de miembros no puede ser negativo")
    private int numMiembros;

    @PositiveOrZero(message = "El número de productos no puede ser negativo")
    private int numProductos;
    
    public ListaCompraResumenDTO() {}

	public ListaCompraResumenDTO(Integer id,
			@NotBlank(message = "El nombre de la lista no puede estar vacío") String nombre,
			@NotBlank(message = "El nombre del propietario no puede estar vacío") String propietarioNombre,
			int numMiembros, int numProductos) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.propietarioNombre = propietarioNombre;
		this.numMiembros = numMiembros;
		this.numProductos = numProductos;
	}

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

	public String getPropietarioNombre() {
		return propietarioNombre;
	}

	public void setPropietarioNombre(String propietarioNombre) {
		this.propietarioNombre = propietarioNombre;
	}

	public int getNumMiembros() {
		return numMiembros;
	}

	public void setNumMiembros(int numMiembros) {
		this.numMiembros = numMiembros;
	}

	public int getNumProductos() {
		return numProductos;
	}

	public void setNumProductos(int numProductos) {
		this.numProductos = numProductos;
	};
}