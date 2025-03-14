package edu.uclm.esi.listacompra.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String email;
    private boolean esPremium;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaCompra> listasCreadas = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "miembros_lista",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "lista_id")
    )
    private List<ListaCompra> listasComoMiembro = new ArrayList<>();

    public Usuario() {}

    public Usuario(String nombre, String email, boolean esPremium) {
        this.nombre = nombre;
        this.email = email;
        this.esPremium = esPremium;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEsPremium() {
		return esPremium;
	}

	public void setEsPremium(boolean esPremium) {
		this.esPremium = esPremium;
	}

	public List<ListaCompra> getListasCreadas() {
		return listasCreadas;
	}

	public void setListasCreadas(List<ListaCompra> listasCreadas) {
		this.listasCreadas = listasCreadas;
	}

	public List<ListaCompra> getListasComoMiembro() {
		return listasComoMiembro;
	}

	public void setListasComoMiembro(List<ListaCompra> listasComoMiembro) {
		this.listasComoMiembro = listasComoMiembro;
	}    
}