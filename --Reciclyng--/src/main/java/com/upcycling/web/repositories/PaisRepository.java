package com.upcycling.web.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, String>, PagingAndSortingRepository<Pais, String> {

	@Query("SELECT a from Pais a WHERE a.baja IS NULL ORDER BY a.nombre")
	public List<Pais> buscarActivos();
	
	@Query("SELECT a from Pais a WHERE a.id = :id")
	public Pais buscarPorId(@Param("id") String id);

	@Query("SELECT a from Pais a WHERE a.baja IS NULL AND a.nombre = :nombre")
	public List<Pais> buscarPaisPorNombre(@Param("nombre") String nombre);
	
	@Query("SELECT a from Pais a WHERE a.baja IS NULL AND a.nombre LIKE :q")
	public Page<Pais> buscarActivos(Pageable pageable,  @Param("q") String q);
	
	@Query("SELECT a from Pais a WHERE a.baja IS NULL order by a.nombre")
	public Page<Pais> buscarActivos(Pageable pageable);
	
	@Query("SELECT a from Pais a WHERE a.baja IS NULL AND a.nombre = :nombre AND a.id != :idPais")
	public List<Pais> buscarPaisPorNombreYDistintoIdPais(@Param("nombre") String nombre,@Param("idPais") String idPais);

}

