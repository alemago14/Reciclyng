package com.upcycling.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upcycling.web.converters.PaisConverter;
import com.upcycling.web.entities.Pais;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.PaisModel;
import com.upcycling.web.repositories.PaisRepository;

@Service
public class PaisService {

	@Autowired
	private PaisConverter paisConverter;

	@Autowired
	private PaisRepository paisRepository;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Pais guardar(PaisModel model) throws WebException {
		Pais pais = paisConverter.modeloToEntidad(model);
		validar(pais);
		pais.setModificacion(new Date());
		return paisRepository.save(pais);
	}

	private void validar(Pais pais) throws WebException{
		
		if (pais.getBaja() != null) {
			throw new WebException("El país que intenta modificar se encuentra dada de baja.");
		}

		if (pais.getNombre() == null || pais.getNombre().isEmpty()) {
			throw new WebException("El nombre del país no puede ser vacío.");
		}
		
		List<Pais> paisExiste = null;
		if(pais.getId() != null && !pais.getId().isEmpty()) {
			paisExiste = paisRepository.buscarPaisPorNombreYDistintoIdPais(pais.getNombre(), pais.getId());
		}else {
			paisExiste = paisRepository.buscarPaisPorNombre(pais.getNombre());
		}
		
		if(!paisExiste.isEmpty()) {
			throw new WebException("Ya existe un país con ese nombre.");
		} 
		
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Pais eliminar(String id) throws WebException {
		Pais pais = paisRepository.buscarPorId(id);
		if (pais.getBaja() == null) {
			pais.setBaja(new Date());
			pais = paisRepository.save(pais);
		} else {
			throw new WebException("El país que intenta eliminar ya se encuentra dado de baja.");
		}
		
		return pais;
	}

	public Page<Pais> listarActivos(Pageable paginable, String q) {
		if((q != null && !q.isEmpty())) {
			return paisRepository.buscarActivos(paginable, "%" + q + "%");
		}
		return paisRepository.buscarActivos(paginable);		
	}
	
	public List<Pais> listarActivos() {
		return paisRepository.buscarActivos();		
	}


	public PaisModel buscar(String id) {
		Pais pais = paisRepository.buscarPorId(id);
		return paisConverter.entidadToModelo(pais);
	}

}