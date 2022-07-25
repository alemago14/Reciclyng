package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Provincia;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.ProvinciaModel;
import com.upcycling.web.repositories.PaisRepository;
import com.upcycling.web.repositories.ProvinciaRepository;

@Component
public class ProvinciaConverter extends Convertidor<ProvinciaModel, Provincia>{
	
	@Autowired
	private ProvinciaRepository provinciaRepository;
	
	@Autowired
	private PaisRepository paisRepository;
	
	@Autowired
	private PaisConverter paisConverter;
	
	public ProvinciaModel entidadToModelo(Provincia provincia){
		ProvinciaModel model = new ProvinciaModel();
		try {
			BeanUtils.copyProperties(provincia, model);
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de provincia", e);
		}
		
		if(provincia.getPais() != null) {
			model.setPaisModel(paisConverter.entidadToModelo(provincia.getPais()));
		}
		
		return model;
	}
	
	public Provincia modeloToEntidad(ProvinciaModel model){
		Provincia provincia = new Provincia();
		if(model.getId() != null && !model.getId().isEmpty()){
			provincia = provinciaRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, provincia);
		} catch (Exception e) {
			log.error("Error al convertir el modelo la provincia en entidad", e);
		}
		
		if(model.getPaisModel() != null) {
			provincia.setPais(paisRepository.buscarPorId(model.getPaisModel().getId()));
		}
		
		return provincia;
	}
	
	public List<ProvinciaModel> entidadesToModelos(List<Provincia> provincias ){
		List<ProvinciaModel> model = new ArrayList<>();
		for(Provincia provincia : provincias){
			model.add(entidadToModelo(provincia));
		}
		return model;
	}
	
	public List<ComboModel> combo(List<Provincia> provincias ){
		List<ComboModel> model = new ArrayList<>();
		for(Provincia provincia : provincias){
			model.add(new ComboModel(provincia.getId(), provincia.getNombre()));
		}
		return model;
	}
	
} 
