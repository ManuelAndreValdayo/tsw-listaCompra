package edu.uclm.esi.listacompra.dao;

import edu.uclm.esi.listacompra.entities.ListaCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ListaCompraDAO extends JpaRepository<ListaCompra, Integer> {

    // Busca listas donde el usuario es propietario o miembro
    @Query("SELECT l FROM ListaCompra l LEFT JOIN l.miembros m WHERE l.propietario.id = :usuarioId OR m.id = :usuarioId")
    List<ListaCompra> findByPropietarioOrMiembro(@Param("usuarioId") Integer usuarioId);

    // Busca listas donde el usuario es propietario
    List<ListaCompra> findByPropietario_Id(Integer usuarioId);

    // Actualizar nombre de una lista
    @Modifying
    @Transactional
    @Query("UPDATE ListaCompra l SET l.nombre = :nombre WHERE l.id = :id")
    int updateNombreLista(@Param("id") Integer id, @Param("nombre") String nombre);
}