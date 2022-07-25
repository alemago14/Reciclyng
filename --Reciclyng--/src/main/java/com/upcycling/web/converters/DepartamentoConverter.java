package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Departamento;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.DepartamentoModel;
import com.upcycling.web.repositories.DepartamentoRepository;
import com.upcycling.web.repositories.ProvinciaRepository;


@Component
public class DepartamentoConverter extends Convertidor<DepartamentoModel, Departamento>{
	
	@Autowired
	private DepartamentoRepository departamentoRepository;
	
	@Autowired
	private ProvinciaConverter provinciaConverter;
	
	@Autowired
	private ProvinciaRepository provinciaRepository;
	
	
	public DepartamentoModel entidadToModelo(Departamento departamento){
		DepartamentoModel model = new DepartamentoModel();
		try {
			BeanUtils.copyProperties(departamento, model);
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de departamento", e);
		}
		
		if(departamento.getProvincia() != null) {
			model.setProvinciaModel(provinciaConverter.entidadToModelo(departamento.getProvincia()));
		}
		return model;
	}
	
	public Departamento modeloToEntidad(DepartamentoModel model){
		Departamento departamento = new Departamento();
		if(model.getId() != null && !model.getId().isEmpty()){
			departamento = departamentoRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, departamento);
		} catch (Exception e) {
			log.error("Error al convertir el modelo de departamento en entidad", e);
		}
		
		if(model.getProvinciaModel() != null) {
			departamento.setProvincia(provinciaRepository.buscarPorId(model.getProvinciaModel().getId()));
		}

		return departamento;
	}
	
	public List<DepartamentoModel> entidadesToModelos(List<Departamento> departamentos ){
		List<DepartamentoModel> model = new ArrayList<>();
		for(Departamento departamento : departamentos){
			model.add(entidadToModelo(departamento));
		}
		return model;
	}
	
	public List<ComboModel> combo(List<Departamento> departamentos ){
		List<ComboModel> model = new ArrayList<>();
		for(Departamento departamento : departamentos){
			model.add(new ComboModel(departamento.getId(), departamento.getNombre()));
		}
		return model;
	}
	
} 
