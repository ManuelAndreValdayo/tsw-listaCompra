package edu.uclm.esi.listacompra.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Producto;

@Repository
public interface ProductoDAO extends JpaRepository <Producto, Integer>{    
    @Query("SELECT p.Id FROM Producto p WHERE p.Nombre = :nombre")
    Optional<Integer> findProductByName(@Param("nombre") String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.id_lista_compra = :id_lista_compra")
    Optional <List<Producto>> findProductByIdLista(@Param("id_lista_compra") Integer id_lista_compra);
    
    @Query("SELECT p.Id FROM Producto p WHERE p.Nombre = :nombre AND p.id_lista_compra = :id_lista_compra")
    Optional<Integer> findProductByIdLista(@Param("nombre") String nombre, @Param("id_lista_compra") int id_lista_compra);
    
    @Query("SELECT p FROM Producto p WHERE p.Nombre = :nombre")
    Producto getProduct(@Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.Nombre = :#{#producto.nombre}, p.Cantidad = :#{#producto.cantidad} WHERE p.Id = :#{#producto.id}")
    Integer modificar(@Param("producto") Producto producto);
}
