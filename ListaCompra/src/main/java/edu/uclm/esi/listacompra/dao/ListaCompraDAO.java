package edu.uclm.esi.listacompra.dao;

import edu.uclm.esi.listacompra.entities.ListaCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaCompraDAO extends JpaRepository<ListaCompra, Integer> {

	// Busca listas donde el usuario es propietario o miembro
	@Query("SELECT l FROM ListaCompra l WHERE l.propietarioId = :usuarioId OR :usuarioId MEMBER OF l.miembrosIds")
	List<ListaCompra> findByPropietarioOrMiembro(@Param("usuarioId") Integer usuarioId);

	// Busca listas donde el usuario es propietario
	List<ListaCompra> findByPropietario_Id(Integer usuarioId);
}