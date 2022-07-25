package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.OrganizacionModel;
import com.upcycling.web.repositories.OrganizacionRepository;

@Component
public class OrganizacionConverter extends Convertidor<OrganizacionModel, Organizacion>{
	
	@Autowired
	private OrganizacionRepository organizacionRepository;
	
	public OrganizacionModel entidadToModelo(Organizacion organizacion){
		OrganizacionModel model = new OrganizacionModel();
		try {
			BeanUtils.copyProperties(organizacion, model);
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de organización", e);
		}
		
		return model;
	}
	
	public Organizacion modeloToEntidad(OrganizacionModel model){
		Organizacion organizacion = new Organizacion();
		if(model.getId() != null && !model.getId().isEmpty()){
			organizacion = organizacionRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, organizacion);
		} catch (Exception e) {
			log.error("Error al convertir el modelo la organización en entidad", e);
		}
		
		return organizacion;
	}
	
	public List<OrganizacionModel> entidadesToModelos(List<Organizacion> Organizacions ){
		List<OrganizacionModel> model = new ArrayList<>();
		for(Organizacion Organizacion : Organizacions){
			model.add(entidadToModelo(Organizacion));
		}
		return model;
	}
	
	public List<ComboModel> combo(List<Organizacion> organizaciones ){
		List<ComboModel> model = new ArrayList<>();
		for(Organizacion Organizacion : organizaciones){
			model.add(new ComboModel(Organizacion.getId(), Organizacion.getNombre()));
		}
		return model;
	}
	
} 
