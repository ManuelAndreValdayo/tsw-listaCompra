package edu.uclm.esi.listacompra.dao;

import edu.uclm.esi.listacompra.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoDAO extends JpaRepository<Producto, Integer> {

    // Buscar productos por nombre
    Optional<Producto> findByNombre(String nombre);

    // Buscar productos por lista de compra
    List<Producto> findByListaCompra_Id(Integer listaCompraId);

    // Buscar un producto específico dentro de una lista
    Optional<Producto> findByNombreAndListaCompra_Id(String nombre, Integer listaCompraId);

    // Eliminar un producto por su ID y su lista de compra
    void deleteByIdAndListaCompra_Id(Integer id, Integer listaCompraId);

    // Actualizar el nombre y cantidad de un producto
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.nombre = :nombre, p.cantidadTotal = :cantidad WHERE p.id = :id")
    int modificarProducto(@Param("id") Integer id, @Param("nombre") String nombre, @Param("cantidad") int cantidad);
}
