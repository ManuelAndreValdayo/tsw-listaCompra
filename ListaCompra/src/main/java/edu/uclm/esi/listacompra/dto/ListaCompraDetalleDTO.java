package edu.uclm.esi.listacompra.dto;
import java.util.List;

import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ListaCompraDetalleDTO {
	private Integer id;

    @NotBlank(message = "El nombre de la lista no puede estar vacío")
    private String nombre;

    @NotNull(message = "El ID del propietario es obligatorio")
    @Positive(message = "El ID del propietario debe ser un número válido")
    private UsuarioDTO propietario;

    private List<@Valid UsuarioDTO> miembros;

    private Page<@Valid ProductoDTO> productos; // Valida cada ProductoDTO en la lista
    
    public ListaCompraDetalleDTO() {}

	public ListaCompraDetalleDTO(Integer id,
			@NotBlank(message = "El nombre de la lista no puede estar vacío") String nombre,
			@NotNull(message = "El ID del propietario es obligatorio") @Positive(message = "El ID del propietario debe ser un número válido") UsuarioDTO propietario,
			@NotNull(message = "La lista de miembros no puede ser nula") List<@Valid UsuarioDTO> miembros,
			Page<@Valid ProductoDTO> productos) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.propietario = propietario;
		this.miembros = miembros;
		this.productos = productos;
	}
}