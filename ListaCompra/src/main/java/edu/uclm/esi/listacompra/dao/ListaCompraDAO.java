package edu.uclm.esi.listacompra.dao;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.listacompra.entities.ListaCompra;

@Repository
public interface ListaCompraDAO extends JpaRepository <ListaCompra, Integer>{

    @Query("SELECT l FROM ListaCompra l WHERE l.id_usuario = :idUsuario")
    Optional<List<ListaCompra>> findByUserId(@Param("idUsuario") int idUsuario);
    
    @Modifying
    @Transactional
    @Query("UPDATE ListaCompra l SET l.Nombre = :#{#listaCompra.nombre} WHERE l.Id = :#{#listaCompra.id}")
    Integer updateLista(@Param("listaCompra") ListaCompra listaCompra);
}
