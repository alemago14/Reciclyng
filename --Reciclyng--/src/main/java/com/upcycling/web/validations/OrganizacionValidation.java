package com.upcycling.web.validations;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.exceptions.OrganizacionException;
import com.upcycling.web.repositories.OrganizacionRepository;


@Component
public class OrganizacionValidation {
	
	private OrganizacionRepository organizacionRepository;
	
	public void validar (String nombre, String domicilio, String mail) throws OrganizacionException{
		
		if(nombre.isEmpty()) {
			throw new OrganizacionException("El nombre no puede estar vacío");
		}
		if(domicilio.isEmpty()) {
			throw new OrganizacionException("El domicilio no puede estar vacío");
		}
		if(mail.isEmpty()) {
			throw new OrganizacionException("El mail está vacío");
		}
		
		EmailValidator mailValidator = new EmailValidator();
		
		//validamos que el mail ingresado sea correcto
		
		if(!mailValidator.isValid(mail, null)) {
			throw new OrganizacionException("El mail ingresado no es valido");
		}
		
		//validamos que el mail no exista anteriormente en la base
		Organizacion thisOrg = organizacionRepository.buscarPorMail(mail);
		if(thisOrg != null) {
			throw new OrganizacionException("El mail ingresado ya está en uso");
		}
		
	}
	
	public void validarId(String id) throws OrganizacionException {
		if (id.isEmpty() || id == null) {
			throw new OrganizacionException("El id de la organización no existe o es incorrecto");
		}
	}
}
