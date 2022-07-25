package com.upcycling.web.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
	
	@Query("SELECT u FROM Usuario u WHERE u.baja is null and u.nombre= :nombre")
	public Usuario buscarPorNombre(@Param("nombre") String nombre);
	@Query("SELECT u FROM Usuario u WHERE u.baja is null and u.apellido= :apellido")
	public Usuario buscarPorApellido(@Param("apellido") String apellido);
	@Query("SELECT u FROM Usuario u WHERE u.baja is null and u.mail= :mail")
	public Usuario buscarPorMail(@Param("mail") String mail);
	@Query("select a from Usuario a WHERE a.baja IS NULL AND a.userName = :usuario")
	public Usuario buscarPorUsuario(@Param("usuario") String usuario);
	@Query("select a from Usuario a WHERE a.baja IS NULL AND a.id = :id")
	public Usuario buscarPorId(@Param("id") String id);
	
	@Query("select a from Usuario a WHERE a.baja IS NULL AND (a.userName LIKE :q OR a.mail LIKE :q OR a.nombre LIKE :q or a.apellido LIKE :q) order by a.apellido,a.nombre ")
	public Page<Usuario> listarActivosAdmin(Pageable pageable, @Param("q") String q);
	
	@Query("select a from Usuario a WHERE a.baja IS NULL")
	public Page<Usuario> listarActivosAdmin(Pageable pageable);
	
	@Query("select a from Usuario a WHERE a.baja IS NULL AND a.organizacion.id = :idOrganizacion")
	public Page<Usuario> listarActivos(Pageable pageable, @Param("idOrganizacion") String idOrganizacion);

	@Query("select a from Usuario a WHERE a.baja IS NULL AND a.organizacion.id = :idOrganizacion AND (a.userName LIKE :q OR a.mail LIKE :q OR a.nombre LIKE :q or a.apellido LIKE :q) order by a.apellido,a.nombre ")
	public Page<Usuario> listarActivos(Pageable pageable, @Param("q") String q, @Param("idOrganizacion") String idOrganizacion);
}
