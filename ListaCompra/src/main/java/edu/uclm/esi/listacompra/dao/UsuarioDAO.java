package edu.uclm.esi.listacompra.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Integer> {

    // Buscar un usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si un usuario es premium
    @Query("SELECT u.esPremium FROM Usuario u WHERE u.id = :id")
    boolean isPremium(@Param("id") Integer id);

    // Buscar todas las listas de un usuario (propietario o miembro)
    @Query("SELECT DISTINCT l FROM ListaCompra l LEFT JOIN l.miembros m WHERE l.propietario.id = :id OR m.id = :id")
    List<ListaCompra> findListasByUsuario(@Param("id") Integer id);
}
