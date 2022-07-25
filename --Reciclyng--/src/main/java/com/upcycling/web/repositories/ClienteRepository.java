package com.upcycling.web.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String>{
	
	@Query("SELECT c FROM Cliente c WHERE c.email = :email")
	public Optional<Cliente> findByEmail(@Param("email") String email);
	
	@Query("SELECT a from Cliente a WHERE a.id = :id")
	public Cliente buscarPorId(@Param("id") String id);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL")
	public Page<Cliente> buscarActivos(Pageable pageable);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL and (a.nombre LIKE :q or a.email like :q or a.organizacion.nombre like :q) order by a.nombre")
	public Page<Cliente> buscarActivos(Pageable pageable,@Param("q") String q);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL ORDER BY a.nombre")
	public List<Cliente> buscarActivos();
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL AND a.organizacion.id = :idOrg")
	public List<Cliente> buscarClientePorOrganizacion(@Param("idOrg") String idOrg);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL and (a.nombre LIKE :q or a.email like :q or a.organizacion.nombre like :q) order by a.nombre")
	public List<Cliente> buscarActivos(@Param("q") String q);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL AND a.nombre = :nombre and a.organizacion.id = :idOrganizacion")
	public List<Cliente> buscarClientePorNombreYOrganizacion(@Param("nombre") String nombre,@Param("idOrganizacion") String idOrganizacion);
	
	@Query("SELECT a from Cliente a WHERE a.baja IS NULL AND a.nombre = :nombre and a.organizacion.id = :idOrganizacion AND a.id != :id")
	public List<Cliente> buscarClientePorNombreYOrganizacionYDistintoIdCliente(@Param("nombre") String nombre,@Param("idOrganizacion") String idOrganizacion,@Param("id") String id);
}
