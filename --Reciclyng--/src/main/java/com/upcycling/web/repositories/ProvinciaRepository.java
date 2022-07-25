package com.upcycling.web.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upcycling.web.entities.Provincia;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, String>, PagingAndSortingRepository<Provincia, String> {

	@Query("SELECT a from Provincia a WHERE a.baja IS NULL ORDER BY a.nombre")
	public List<Provincia> buscarActivos();
	
	@Query("SELECT a from Provincia a WHERE a.baja IS NULL AND a.pais.id = :idPais")
	public List<Provincia> buscarProvinciaPorPais(@Param("idPais") String idPais);
	
	@Query("SELECT a from Provincia a WHERE a.baja IS NULL AND (a.nombre LIKE :q OR a.pais.nombre LIKE :q)")
	public Page<Provincia> buscarActivos(Pageable pageable,  @Param("q") String q);
	
	@Query("SELECT a from Provincia a WHERE a.baja IS NULL")
	public Page<Provincia> buscarActivos(Pageable pageable);

	@Query("SELECT a from Provincia a WHERE a.baja IS NULL AND a.nombre = :nombre AND a.pais.id = :id")
	public List<Provincia> buscarProvinciaPorPaisYNombre(@Param("nombre") String nombre, @Param("id") String id);
	
	@Query("SELECT a from Provincia a WHERE a.id = :id")
	public Provincia buscarPorId(@Param("id") String id);
	
	@Query("SELECT a from Provincia a WHERE a.baja IS NULL AND a.nombre = :nombre and a.pais.id = :idPais")
	public List<Provincia> buscarProvinciaPorNombreYPais(@Param("nombre") String nombre,@Param("idPais") String idPais);
	
	@Query("SELECT a from Provincia a WHERE a.baja IS NULL AND a.nombre = :nombre and a.pais.id = :idPais AND a.id != :id")
	public List<Provincia> buscarProvinciaPorNombreYPaisYDistintoIdProvincia(@Param("nombre") String nombre,@Param("idPais") String idPais,@Param("id") String id);

}

