package com.upcycling.web.validations;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Usuario;
import com.upcycling.web.enums.Rol;
import com.upcycling.web.exceptions.UsuarioException;
import com.upcycling.web.repositories.UsuarioRepository;


@Component
public class UsuarioValidation {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public void validar(String nombre, String apellido, String mail, Rol rol) throws UsuarioException {

		if (nombre.isEmpty()) {
			throw new UsuarioException("El nombre no puede estar vacio");
		}
		if (apellido.isEmpty()) {
			throw new UsuarioException("El apellido no puede estar vacío");
		}
		if (mail.isEmpty()) {
			throw new UsuarioException("El mail no puede estar vacio");
		}

		EmailValidator mailValidator = new EmailValidator();

		// validamos que el mail ingresado sea correcto

		if (!mailValidator.isValid(mail, null)) {
			throw new UsuarioException("El mail ingresado no es valido");
		}

		// validamos que el mail no exista anteriormente en la base
		
		Usuario thisUser = usuarioRepository.buscarPorMail(mail);
		
		if (thisUser != null) {
			throw new UsuarioException("El mail ingresado ya está en uso");
		}

		if (rol == null) {
			throw new UsuarioException("No se ha asignado un rol al usuario");
		}
	}

	public void validarId(String id) throws UsuarioException {
		
		if (id.isEmpty() || id == null) {
			throw new UsuarioException("El id del usuario no existe o es incorrecto");
		}
	}
}
