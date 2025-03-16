package edu.uclm.esi.listacompra.dto;
import java.util.List;

public class ListaCompraDTO {
    private Integer id;
    private String nombre;
	private Integer propietarioId;
    private List<ProductoDTO> productos; // Solo para devolver listas con productos

    public ListaCompraDTO() {}

    public ListaCompraDTO(Integer id, String nombre, Integer propietarioId, List<ProductoDTO> productos) {
        this.id = id;
        this.nombre = nombre;
        this.propietarioId = propietarioId;
        this.productos = productos;
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

	public Integer getPropietarioId() {
		return propietarioId;
	}

	public void setPropietarioId(Integer propietarioId) {
		this.propietarioId = propietarioId;
	}

	public List<ProductoDTO> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoDTO> productos) {
		this.productos = productos;
	}
}