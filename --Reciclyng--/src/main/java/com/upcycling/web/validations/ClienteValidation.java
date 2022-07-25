package com.upcycling.web.validations;

import java.util.Optional;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.upcycling.web.entities.Cliente;
import com.upcycling.web.exceptions.ClienteException;
import com.upcycling.web.repositories.ClienteRepository;

@Component
public class ClienteValidation {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public void validarDatos(String nombre, String email) throws ClienteException{
		
		/* Validamos que tanto nombre como el correo no sean nulos o tengan cadenas vacias */
		
		if(nombre == null || nombre.isEmpty()) {
			throw new ClienteException(ClienteException.NULL_NOMBRE);
		}
		
		if(email == null || email.isEmpty()) {
			throw new ClienteException(ClienteException.NULL_EMAIL);
		}
		
		/* Validamos que el correo sea valido (respete la especificacion RFC 2822) */
		
		EmailValidator emailValidator = new EmailValidator();
		
		if(!emailValidator.isValid(email, null)) {
			throw new ClienteException(ClienteException.ERR_EMAIL_INVALIDO);
		}
		
		/* Validamos que el cliente no exista previamente */
		
		Optional<Cliente> thisCliente = clienteRepository.findByEmail(email);
		if(thisCliente.isPresent()) {
			throw new ClienteException(ClienteException.ERR_EMAIL_EXISTE);
		}
		
	}
	
	public void validarAlModificar(String id, String email, String nombre) throws ClienteException{
		
		Optional<Cliente> thisCliente = clienteRepository.findById(id);
		
		if(thisCliente.isPresent()) {
			/* Validamos que tanto nombre como el correo no sean nulos o tengan cadenas vacias */
			
			if(nombre == null || nombre.isEmpty()) {
				throw new ClienteException(ClienteException.NULL_NOMBRE);
			}
			
			if(email == null || email.isEmpty()) {
				throw new ClienteException(ClienteException.NULL_EMAIL);
			}
			
			/* Validamos que el correo sea valido (respete la especificacion RFC 2822) */
			
			EmailValidator emailValidator = new EmailValidator();
			
			if(!emailValidator.isValid(email, null)) {
				throw new ClienteException(ClienteException.ERR_EMAIL_INVALIDO);
			}
			
			/* Validamos que el nuevo correo no exista previamente en la base de datos */
		
			if(!thisCliente.get().getEmail().equals(email)) {
				
				Optional<Cliente> checkCliente = clienteRepository.findByEmail(email);
				
				if(checkCliente.isPresent()) {
					throw new ClienteException(ClienteException.ERR_EMAIL_EXISTE);
				}
			}
			
		}else {
			throw new ClienteException(ClienteException.ERR_CLIENTE_NO_EXISTE);
		}
		
	}

}
