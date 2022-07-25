package com.upcycling.web.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.upcycling.web.converters.ClienteConverter;
import com.upcycling.web.entities.Cliente;
import com.upcycling.web.exceptions.ClienteException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ClienteModel;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.repositories.ClienteRepository;
import com.upcycling.web.validations.ClienteValidation;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ClienteValidation clienteValidation;
	
	@Autowired
	private ClienteConverter clienteConverter;
	
	public void crearCliente(String nombre, String email) throws ClienteException{
		
		clienteValidation.validarDatos(nombre, email);
		
		Cliente cliente = new Cliente();
		cliente.setNombre(nombre);
		cliente.setEmail(email);
		
		clienteRepository.save(cliente);
	}
	
	public void modificarCliente(String id, String email, String nombre) throws ClienteException{
		
		clienteValidation.validarAlModificar(id, email, nombre);
		
		Cliente cliente = clienteRepository.getById(id);
		cliente.setEmail(email);
		cliente.setNombre(nombre);
		clienteRepository.save(cliente);
	}
	
	public void eliminarCliente(String id){
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		
		if(cliente.isPresent()) {
			clienteRepository.delete(cliente.get());
		}else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	/* metodos para listar clientes */
	
	public List<Cliente> listarClientes(){
		return clienteRepository.findAll();
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Cliente guardar(ClienteModel model) throws WebException {
		Cliente cliente = clienteConverter.modeloToEntidad(model);
		validar(cliente);
		cliente.setModificacion(new Date());
		return clienteRepository.save(cliente);
	}

	private void validar(Cliente cliente) throws WebException {

		if (cliente.getBaja() != null) {
			throw new WebException("La cliente que intenta modificar se encuentra dado de baja.");
		}

		if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
			throw new WebException("El nombre del cliente no puede ser vacío.");
		}

		if (cliente.getOrganizacion() == null) {
			throw new WebException("El cliente debe estar vinculado a una organización.");
		}
		List<Cliente> clienteExiste = null;

		if (cliente.getId() != null && !cliente.getId().isEmpty()) {
			clienteExiste = clienteRepository.buscarClientePorNombreYOrganizacionYDistintoIdCliente(cliente.getNombre(),cliente.getOrganizacion().getId(),cliente.getId());
		} else {
			clienteExiste = clienteRepository.buscarClientePorNombreYOrganizacion(cliente.getNombre(),cliente.getOrganizacion().getId());
		}

		if (!clienteExiste.isEmpty()) {
			throw new WebException("Ya existe un cliente con ese nombre.");
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { WebException.class, Exception.class })
	public Cliente eliminar(String id) throws WebException {
		Cliente cliente = clienteRepository.buscarPorId(id);
		if (cliente.getBaja() == null) {
			cliente.setBaja(new Date());
			cliente = clienteRepository.save(cliente);
		} else {
			throw new WebException("La cliente que intenta eliminar ya se encuentra dada de baja.");
		}

		return cliente;
	}

	public Page<Cliente> listarActivos(Pageable paginable, String q) {
		if ((q != null && !q.isEmpty())) {
			return clienteRepository.buscarActivos(paginable, "%" + q + "%");
		}
		return clienteRepository.buscarActivos(paginable);
	}

	public List<Cliente> listarClientePorOrganizacion(String id) {
		return clienteRepository.buscarClientePorOrganizacion(id);
	}

	public ClienteModel buscar(String id) {
		Cliente cliente = clienteRepository.buscarPorId(id);
		return clienteConverter.entidadToModelo(cliente);
	}
	
	public Cliente buscarPorId(String id) {
		return clienteRepository.buscarPorId(id);
	}
	
	public List<ComboModel> listarActivos(String id) {
		List<Cliente> clientes = clienteRepository.buscarClientePorOrganizacion(id);
		return clienteConverter.combo(clientes);
	}
	
	
}
