package com.upcycling.web.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upcycling.web.converters.DepartamentoConverter;
import com.upcycling.web.entities.Departamento;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.DepartamentoModel;
import com.upcycling.web.repositories.DepartamentoRepository;

@Service
public class DepartamentoService {

	@Autowired
	private DepartamentoConverter departamentoConverter;

	@Autowired
	private DepartamentoRepository departamentoRepository;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Departamento guardar(DepartamentoModel model) throws WebException {
		Departamento departamento = departamentoConverter.modeloToEntidad(model);
		validar(departamento);
		departamento.setModificacion(new Date());
		return departamentoRepository.save(departamento);
	}

	private void validar(Departamento departamento) throws WebException {

		if (departamento.getBaja() != null) {
			throw new WebException("El departamento que intenta modificar se encuentra dada de baja.");
		}

		if (departamento.getNombre() == null || departamento.getNombre().isEmpty()) {
			throw new WebException("El nombre de el departamento no puede ser vacío.");
		}

		if (departamento.getProvincia() == null) {
			throw new WebException("El departamento debe estar vinculado a una provincia.");
		}

		List<Departamento> departamentoExiste = null;
		if (departamento.getId() != null && !departamento.getId().isEmpty()) {
			departamentoExiste = departamentoRepository.buscarPorNombreYProvinciaYDistintoId(departamento.getNombre(), departamento.getProvincia().getId(), departamento.getId());
		} else {
			departamentoExiste = departamentoRepository.buscarPorNombreYProvincia(departamento.getNombre(), departamento.getProvincia().getId());
		}

		if (!departamentoExiste.isEmpty()) {
			throw new WebException("Ya existe un departamento con ese nombre.");
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Departamento eliminar(String id) throws WebException {
		Departamento departamento = departamentoRepository.buscarPorId(id);
		if (departamento.getBaja() == null) {
			departamento.setBaja(new Date());
			departamento = departamentoRepository.save(departamento);
		} else {
			throw new WebException("EL departamento que intenta eliminar ya se encuentra dado de baja.");
		}

		return departamento;
	}

	public Page<Departamento> listarActivos(Pageable paginable, String q) {
		if ((q != null && !q.isEmpty())) {
			return departamentoRepository.buscarActivos(paginable, "%" + q + "%");
		}
		return departamentoRepository.buscarActivos(paginable);
	}

	public DepartamentoModel buscar(String id) {
		Departamento departamento = departamentoRepository.buscarPorId(id);
		return departamentoConverter.entidadToModelo(departamento);
	}
	
	public List<ComboModel> listarActivosPorProvincia(String id) {
		List<Departamento> departamentos = departamentoRepository.buscarPorProvincia(id);
		return departamentoConverter.combo(departamentos);
	}

}