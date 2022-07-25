package com.upcycling.web.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Usuario;
import com.upcycling.web.models.UsuarioModel;
import com.upcycling.web.repositories.OrganizacionRepository;
import com.upcycling.web.repositories.UsuarioRepository;

@Component("usuarioConverter")
public class UsuarioConverter extends Convertidor<UsuarioModel, Usuario> {

	@Autowired
	@Qualifier("usuarioRepository")
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private OrganizacionRepository organizacionRepository;

	public UsuarioModel entidadToModelo(Usuario usuario) {
		UsuarioModel model = new UsuarioModel();
		try {
			BeanUtils.copyProperties(usuario, model);
			
			if(usuario.getOrganizacion() != null) {
				model.setIdOrganizacion(usuario.getOrganizacion().getId());
			}
		} catch (Exception e) {
			log.error("Error al convertir la entidad en el modelo de usuario", e);
		}
		
		return model;
	}

	public Usuario modeloToEntidad(UsuarioModel model) {
		Usuario usuario = new Usuario();
		if (model.getId() != null && !model.getId().isEmpty()) {
			usuario = usuarioRepository.buscarPorId(model.getId());
		}
		
		try {
			BeanUtils.copyProperties(model, usuario);
		} catch (Exception e) {
			log.error("Error al convertir el modelo del usuario en entidad", e);
		}
		
		if(model.getIdOrganizacion() != null) {
			usuario.setOrganizacion(organizacionRepository.buscarPorId(model.getIdOrganizacion()));
		}

		return usuario;
	}

	public List<UsuarioModel> entidadesToModelos(List<Usuario> usuarios) {
		List<UsuarioModel> model = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			model.add(entidadToModelo(usuario));
		}
		return model;
	}

}
