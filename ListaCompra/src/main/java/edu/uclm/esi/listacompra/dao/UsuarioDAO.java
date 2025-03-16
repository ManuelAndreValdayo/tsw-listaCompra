package edu.uclm.esi.listacompra.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.listacompra.entities.ListaCompra;
import edu.uclm.esi.listacompra.entities.Usuario;

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
