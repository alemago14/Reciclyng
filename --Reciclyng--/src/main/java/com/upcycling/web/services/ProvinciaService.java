package com.upcycling.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upcycling.web.converters.ProvinciaConverter;
import com.upcycling.web.entities.Provincia;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.ProvinciaModel;
import com.upcycling.web.repositories.ProvinciaRepository;

@Service
public class ProvinciaService {

	@Autowired
	private ProvinciaConverter provinciaConverter;

	@Autowired
	private ProvinciaRepository provinciaRepository;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Provincia guardar(ProvinciaModel model) throws WebException {
		Provincia provincia = provinciaConverter.modeloToEntidad(model);
		validar(provincia);
		provincia.setModificacion(new Date());
		return provinciaRepository.save(provincia);
	}

	private void validar(Provincia provincia) throws WebException {

		if (provincia.getBaja() != null) {
			throw new WebException("La provincia que intenta modificar se encuentra dada de baja.");
		}

		if (provincia.getNombre() == null || provincia.getNombre().isEmpty()) {
			throw new WebException("El nombre de la provincia no puede ser vacío.");
		}

		if (provincia.getPais() == null) {
			throw new WebException("La provincia debe estar vinculada a un país.");
		}
		List<Provincia> provinciaExiste = null;

		if (provincia.getId() != null && !provincia.getId().isEmpty()) {
			provinciaExiste = provinciaRepository.buscarProvinciaPorNombreYPaisYDistintoIdProvincia(provincia.getNombre(),provincia.getPais().getId(),provincia.getId());
		} else {
			provinciaExiste = provinciaRepository.buscarProvinciaPorNombreYPais(provincia.getNombre(),provincia.getPais().getId());
		}

		if (!provinciaExiste.isEmpty()) {
			throw new WebException("Ya existe una provincia con ese nombre.");
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Provincia eliminar(String id) throws WebException {
		Provincia provincia = provinciaRepository.buscarPorId(id);
		if (provincia.getBaja() == null) {
			provincia.setBaja(new Date());
			provincia = provinciaRepository.save(provincia);
		} else {
			throw new WebException("La provincia que intenta eliminar ya se encuentra dada de baja.");
		}

		return provincia;
	}

	public Page<Provincia> listarActivos(Pageable paginable, String q) {
		if ((q != null && !q.isEmpty())) {
			return provinciaRepository.buscarActivos(paginable, "%" + q + "%");
		}
		return provinciaRepository.buscarActivos(paginable);
	}

	public List<Provincia> listarProvinciaPorPais(String id) {
		return provinciaRepository.buscarProvinciaPorPais(id);
	}

	public ProvinciaModel buscar(String id) {
		Provincia provincia = provinciaRepository.buscarPorId(id);
		return provinciaConverter.entidadToModelo(provincia);
	}
	
	public Provincia buscarPorId(String id) {
		return provinciaRepository.buscarPorId(id);
	}
	
	public List<ComboModel> listarActivos(String id) {
		List<Provincia> provincias = provinciaRepository.buscarProvinciaPorPais(id);
		return provinciaConverter.combo(provincias);
	}

}