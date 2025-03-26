package edu.uclm.esi.listacompra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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
}