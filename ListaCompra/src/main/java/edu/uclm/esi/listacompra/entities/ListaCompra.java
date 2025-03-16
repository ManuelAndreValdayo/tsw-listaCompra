package edu.uclm.esi.listacompra.entities;

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
		this.nombre = nombre;
		this.propietarioId = propietarioId;
		this.fechaCreacion = LocalDateTime.now();
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

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Integer getPropietarioId() {
		return propietarioId;
	}

	public void setPropietarioId(Integer propietarioId) {
		this.propietarioId = propietarioId;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public List<Integer> getMiembrosIds() {
		return miembrosIds;
	}

	public void setMiembrosIds(List<Integer> miembrosIds) {
		this.miembrosIds = miembrosIds;
	}
}