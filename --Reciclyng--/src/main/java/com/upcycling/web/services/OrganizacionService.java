package com.upcycling.web.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.upcycling.web.converters.OrganizacionConverter;
import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.exceptions.OrganizacionException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.OrganizacionModel;
import com.upcycling.web.repositories.OrganizacionRepository;
import com.upcycling.web.validations.OrganizacionValidation;

@Service
public class OrganizacionService {

	@Autowired
	private OrganizacionRepository organizacionRepository;
	@Autowired
	private OrganizacionValidation organizacionValidation;
	@Autowired
	private OrganizacionConverter organizacionConverter;

	public static final String NOT_FOUND = "No se encontró la organización";

	// REGISTRAR
	public void registrarOrganizacion(String nombre, String domicilio, String mail) throws OrganizacionException {

		/* validacion */
		organizacionValidation.validar(nombre, domicilio, mail);

		Organizacion organizacion = new Organizacion();

		organizacion.setNombre(nombre);
		organizacion.setDomicilio(domicilio);
		organizacion.setMail(mail);

		organizacionRepository.save(organizacion);

	}

	// MODIFICAR
	public void modificarOrganizacion(String id, String nombre, String domicilio, String mail)
			throws OrganizacionException {

		/* Validacion */
		organizacionValidation.validar(nombre, domicilio, mail);

		Optional<Organizacion> respuesta = organizacionRepository.findById(id);

		if (respuesta.isPresent()) {
			Organizacion organizacion = new Organizacion();

			organizacion.setNombre(nombre);
			organizacion.setDomicilio(domicilio);
			organizacion.setMail(mail);

			organizacionRepository.save(organizacion);
		} else {
			throw new OrganizacionException(NOT_FOUND);
		}

	}

	// ELIMINAR
	public void eliminarOrganizacion(String id) throws OrganizacionException {

		Optional<Organizacion> organizacion = organizacionRepository.findById(id);

		if (organizacion.isPresent()) {

			organizacionRepository.delete(organizacion.get());
		} else {
			throw new OrganizacionException(NOT_FOUND);
		}
	}

	// LISTAR

	public List<Organizacion> listarTodos() {

		return organizacionRepository.findAll();
	}

	@SuppressWarnings("deprecation")
	public Organizacion getOne(String id) {
		return organizacionRepository.getOne(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Organizacion guardar(OrganizacionModel model) throws WebException {
		Organizacion organizacion = organizacionConverter.modeloToEntidad(model);
		validar(organizacion);
		organizacion.setModificacion(new Date());
		return organizacionRepository.save(organizacion);
	}
	
	public List<Organizacion> listarActivos() {
		return organizacionRepository.buscarActivos();
	}
	
	private void validar(Organizacion organizacion) throws WebException {

		if (organizacion.getBaja() != null) {
			throw new WebException("La organización que intenta modificar se encuentra dada de baja.");
		}

		if (organizacion.getNombre() == null || organizacion.getNombre().isEmpty()) {
			throw new WebException("El nombre de la organización no puede ser vacío.");
		}

		List<Organizacion> organizacionExiste = null;

		if (organizacion.getId() != null && !organizacion.getId().isEmpty()) {
			organizacionExiste = organizacionRepository.buscarOrganizacionPorNombreYDistintoId(organizacion.getNombre(),organizacion.getId());
		} else {
			organizacionExiste = organizacionRepository.buscarOrganizacionPorNombre(organizacion.getNombre());
		}

		if (!organizacionExiste.isEmpty()) {
			throw new WebException("Ya existe una organización con ese nombre.");
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Organizacion eliminar(String id) throws WebException {
		Organizacion organizacion = organizacionRepository.buscarPorId(id);
		if (organizacion.getBaja() == null) {
			organizacion.setBaja(new Date());
			organizacion = organizacionRepository.save(organizacion);
		} else {
			throw new WebException("La organización que intenta eliminar ya se encuentra dada de baja.");
		}

		return organizacion;
	}

	public Page<Organizacion> listarActivos(Pageable paginable, String q) {
		if ((q != null && !q.isEmpty())) {
			return organizacionRepository.buscarActivos(paginable, "%" + q + "%");
		}
		return organizacionRepository.buscarActivos(paginable);
	}
	
	public OrganizacionModel buscar(String id) {
		Organizacion organizacion = organizacionRepository.buscarPorId(id);
		return organizacionConverter.entidadToModelo(organizacion);
	}
	
	public Organizacion buscarPorId(String id) {
		Organizacion organizacion = organizacionRepository.buscarPorId(id);
		return organizacion;
	}
	
}
