package com.upcycling.web.exceptions;

public class ClienteException extends Exception {
	
	public ClienteException(String exception) {
		super(exception);
	}
	
	/* AQUI VAN TODAS LOS POSIBLES MENSAJES QUE PUEDE ARROJAR ESTA CLASE */
	
	public static final String NULL_NOMBRE = "El nombre no puede estar vacío";
	public static final String NULL_EMAIL = "El correo no puede estar vacío";
	public static final String ERR_EMAIL_EXISTE = "Este correo ya se encuentra registrado";
	public static final String ERR_EMAIL_INVALIDO = "Tiene que introducir un correo valido";
	public static final String ERR_CLIENTE_NO_EXISTE = "Este cliente no existe";
}
