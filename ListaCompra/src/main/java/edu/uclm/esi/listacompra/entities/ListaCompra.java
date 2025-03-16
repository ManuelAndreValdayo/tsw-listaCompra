package edu.uclm.esi.listacompra.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "listas")
public class ListaCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
	private LocalDateTime fechaCreacion;

	@ManyToOne
	@JoinColumn(name = "propietario_id", nullable = false)
	private Usuario propietario;

	@OneToMany(mappedBy = "listaCompra", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Producto> productos = new ArrayList<>();

	@ManyToMany(mappedBy = "listasComoMiembro")
	private List<Usuario> miembros = new ArrayList<>();

	public ListaCompra() {
		this.fechaCreacion = LocalDateTime.now();
	}

	public ListaCompra(String nombre, Usuario propietario) {
		this.nombre = nombre;
		this.propietario = propietario;
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

	public Usuario getPropietario() {
		return propietario;
	}

	public void setPropietario(Usuario propietario) {
		this.propietario = propietario;
	}

	public List<Producto> getProductos() {
		return productos;
	}

	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public List<Usuario> getMiembros() {
		return miembros;
	}

	public void setMiembros(List<Usuario> miembros) {
		this.miembros = miembros;
	}
}