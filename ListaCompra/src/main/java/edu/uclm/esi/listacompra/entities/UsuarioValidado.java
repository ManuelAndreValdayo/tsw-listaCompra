package edu.uclm.esi.listacompra.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioValidado {

    private Integer id;
    private String email;
    private boolean esPremium;

    public UsuarioValidado() {}

    public UsuarioValidado(Integer id, String email, boolean esPremium) {
        this.id = id;
        this.email = email;
        this.esPremium = esPremium;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @JsonProperty("esPremium")
    public boolean isPaidUser() { 
        return esPremium; 
    }
    
    @JsonProperty("esPremium") 
    public void setPaidUser(boolean esPremium) { 
        this.esPremium = esPremium; 
    }
}