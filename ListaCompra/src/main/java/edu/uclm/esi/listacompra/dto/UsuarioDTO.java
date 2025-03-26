package edu.uclm.esi.listacompra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UsuarioDTO {
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número válido")
    private Integer id;

    @NotBlank(message = "El nombre del usuario no puede estar vacío")
    private String nombre;

    private boolean premium;

    public UsuarioDTO() {}

    public UsuarioDTO(Integer id, String nombre, boolean premium) {
        this.id = id;
        this.nombre = nombre;
        this.premium = premium;
    }
}