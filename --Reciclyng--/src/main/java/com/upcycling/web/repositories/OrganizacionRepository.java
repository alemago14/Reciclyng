package com.upcycling.web.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Organizacion;



@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, String> {
	
	@Query("SELECT o FROM Organizacion o WHERE o.nombre= :nombre")
	public Organizacion buscarPorNombre(@Param("nombre") String nombre); 
	@Query("SELECT o FROM Organizacion o WHERE o.mail= :mail")
	public Organizacion buscarPorMail(@Param("mail") String mail);
	
	@Query("SELECT a from Organizacion a WHERE a.id = :id")
	public Organizacion buscarPorId(@Param("id") String id);
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL")
	public Page<Organizacion> buscarActivos(Pageable pageable);
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL and (a.nombre LIKE :q or a.mail like :q) order by a.nombre")
	public Page<Organizacion> buscarActivos(Pageable pageable,@Param("q") String q);
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL ORDER BY a.nombre")
	public List<Organizacion> buscarActivos();
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL and (a.nombre LIKE :q or a.mail like :q) order by a.nombre")
	public List<Organizacion> buscarActivos(@Param("q") String q);
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL and a.nombre = :nombre and a.id != :id")
	public List<Organizacion> buscarOrganizacionPorNombreYDistintoId(@Param("nombre") String nombre,@Param("id") String id);
	
	@Query("SELECT a from Organizacion a WHERE a.baja IS NULL and a.nombre = :nombre")
	public List<Organizacion> buscarOrganizacionPorNombre(@Param("nombre") String nombre);
	
}
