package edu.uclm.esi.listacompra.dao;

import edu.uclm.esi.listacompra.entities.Producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDAO extends JpaRepository<Producto, Integer> {
	// Buscar productos por lista de compra
	List<Producto> findByListaCompra_Id(Integer listaCompraId);
	
	// Debe quedar así
	Page<Producto> findByListaCompra_Id(Integer listaCompraId, Pageable pageable);
	
	@Query("SELECT COUNT(p) FROM Producto p WHERE p.listaCompra.id = :listaId AND p.creadorId = :usuarioId")
	int countByListaAndCreador(@Param("listaId") Integer listaId, @Param("usuarioId") Integer usuarioId);

	// Eliminar un producto por su ID y su lista de compra
	void deleteByIdAndListaCompra_Id(Integer id, Integer listaCompraId);
}
