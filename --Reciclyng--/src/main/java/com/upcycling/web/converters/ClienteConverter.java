package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Cliente;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.ClienteModel;
import com.upcycling.web.repositories.OrganizacionRepository;
import com.upcycling.web.repositories.ClienteRepository;

@Component
public class ClienteConverter extends Convertidor<ClienteModel, Cliente>{
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private OrganizacionRepository organizacionRepository;
	
	@Autowired
	private OrganizacionConverter organizacionConverter;
	
	public ClienteModel entidadToModelo(Cliente cliente){
		ClienteModel model = new ClienteModel();
		try {
			BeanUtils.copyProperties(cliente, model);
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de cliente", e);
		}
		
		if(cliente.getOrganizacion() != null) {
			model.setOrganizacionModel(organizacionConverter.entidadToModelo(cliente.getOrganizacion()));
		}
		
		return model;
	}
	
	public Cliente modeloToEntidad(ClienteModel model){
		Cliente cliente = new Cliente();
		if(model.getId() != null && !model.getId().isEmpty()){
			cliente = clienteRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, cliente);
		} catch (Exception e) {
			log.error("Error al convertir el modelo de cliente en entidad", e);
		}
		
		if(model.getOrganizacionModel() != null) {
			cliente.setOrganizacion(organizacionRepository.buscarPorId(model.getOrganizacionModel().getId()));
		}
		
		return cliente;
	}
	
	public List<ClienteModel> entidadesToModelos(List<Cliente> clientes ){
		List<ClienteModel> model = new ArrayList<>();
		for(Cliente cliente : clientes){
			model.add(entidadToModelo(cliente));
		}
		return model;
	}
	
	public List<ComboModel> combo(List<Cliente> clientes ){
		List<ComboModel> model = new ArrayList<>();
		for(Cliente cliente : clientes){
			model.add(new ComboModel(cliente.getId(), cliente.getNombre()));
		}
		return model;
	}
	
} 
