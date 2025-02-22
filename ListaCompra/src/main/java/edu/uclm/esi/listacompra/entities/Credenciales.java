package edu.uclm.esi.listacompra.entities;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credenciales {
    @JsonProperty("Email")
	private String Email;
    
    @JsonProperty("Pwd1")
	private String Pwd1;
    
    @JsonProperty("Pwd2")
	private String Pwd2;
    
    @JsonProperty("Nombre")
	private String Nombre;
    
    @JsonProperty("Apellidos")
	private String Apellidos;
    
    @JsonProperty("Telefono")
	private String Telefono;
	
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getPwd1() {
		return Pwd1;
	}
	public void setPwd1(String pwd1) {
		Pwd1 = pwd1;
	}
	public String getPwd2() {
		return Pwd2;
	}
	public void setPwd2(String pwd2) {
		Pwd2 = pwd2;
	}
	
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		Nombre = nombre;
	}
	public String getApellidos() {
		return Apellidos;
	}
	public void setApellidos(String apellidos) {
		Apellidos = apellidos;
	}
	public String getTelefono() {
		return Telefono;
	}
	public void setTelefono(String telefono) {
		Telefono = telefono;
	}
	public void comprobar() {
		if(!this.Pwd1.equals(this.Pwd2)) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,"las constraseñas no coinciden");
		}else if(this.Pwd1.length() < 4) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,"La contraseña debe de tener 4 o más caracteres");
		}
	}
}
