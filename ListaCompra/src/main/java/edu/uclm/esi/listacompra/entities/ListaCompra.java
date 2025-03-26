package edu.uclm.esi.listacompra.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity 
@Table(name = "listas")
@Getter @Setter
public class ListaCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private LocalDateTime fechaCreacion;

    @Column(name = "propietario_id", nullable = false)
    private Integer propietarioId;

    @OneToMany(mappedBy = "listaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "lista_miembros", joinColumns = @JoinColumn(name = "lista_id"))
    @Column(name = "miembro_id")
    private List<Integer> miembrosIds = new ArrayList<>();

	public ListaCompra() {
		this.fechaCreacion = LocalDateTime.now();
	}

	public ListaCompra(String nombre, Integer propietarioId) {
		this.fechaCreacion = LocalDateTime.now();
		this.nombre = nombre;
		this.propietarioId = propietarioId;
	}
} 