package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Pais;
import com.upcycling.web.models.PaisModel;
import com.upcycling.web.repositories.PaisRepository;


@Component
public class PaisConverter extends Convertidor<PaisModel, Pais>{
	
	@Autowired
	private PaisRepository paisRepository;
	
	public PaisModel entidadToModelo(Pais pais){
		PaisModel model = new PaisModel();
		try {
			BeanUtils.copyProperties(pais, model);
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de país", e);
		}
		
		return model;
	}
	
	public Pais modeloToEntidad(PaisModel model){
		Pais pais = new Pais();
		if(model.getId() != null && !model.getId().isEmpty()){
			pais = paisRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, pais);
		} catch (Exception e) {
			log.error("Error al convertir el modelo de país en entidad", e);
		}
		
		return pais;
	}
	
	public List<PaisModel> entidadesToModelos(List<Pais> paises ){
		List<PaisModel> model = new ArrayList<>();
		for(Pais pais : paises){
			model.add(entidadToModelo(pais));
		}
		return model;
	}
	
} 