package com.upcycling.web.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, String>, PagingAndSortingRepository<Departamento, String> {

	@Query("SELECT a from Departamento a WHERE a.baja IS NULL")
	public Page<Departamento> buscarActivos(Pageable pageable);
	
	@Query("SELECT a from Departamento a WHERE a.baja IS NULL and (a.nombre LIKE :q or a.provincia.nombre like :q or a.provincia.pais.nombre like :q) order by a.nombre")
	public Page<Departamento> buscarActivos(Pageable pageable,@Param("q") String q);
	
	@Query("SELECT a from Departamento a WHERE a.baja IS NULL ORDER BY a.nombre")
	public List<Departamento> buscarActivos();

	@Query("SELECT a from Departamento a WHERE a.baja IS NULL AND (a.nombre LIKE :nombre OR a.provincia.pais.nombre LIKE :nombre OR a.provincia.nombre LIKE :nombre) AND a.provincia.id = :idProvincia order by a.nombre")
	public Page<Departamento> buscarActivosPorProvinciaYQ(Pageable pageable,  @Param("nombre") String nombre, @Param("idProvincia") String idProvincia);
	
	@Query("SELECT a from Departamento a WHERE a.baja IS NULL AND a.nombre = :nombre AND a.provincia.id = :id order by a.nombre")
	public List<Departamento> buscarPorNombreYProvincia(@Param("nombre") String nombre, @Param("id") String id);

	@Query("SELECT a from Departamento a WHERE a.baja IS NULL AND a.provincia.id = :idProvincia ORDER BY a.nombre")
	public List<Departamento> buscarPorProvincia(@Param("idProvincia") String idProvincia);
	
	@Query("SELECT a from Departamento a WHERE a.id = :id")
	public Departamento buscarPorId(@Param("id") String id);
	
	@Query("SELECT a from Departamento a WHERE a.baja IS NULL AND a.nombre = :nombre and a.provincia.id = :idProvincia AND a.id != :id")
	public List<Departamento> buscarPorNombreYProvinciaYDistintoId(@Param("nombre") String nombre,@Param("idProvincia") String idProvincia,@Param("id") String id);

}

