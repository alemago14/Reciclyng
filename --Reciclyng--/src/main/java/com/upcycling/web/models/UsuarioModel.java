package com.upcycling.web.models;

import java.util.Date;

import com.upcycling.web.enums.Rol;

import lombok.Data;


@Data
public class UsuarioModel {
	private String id;
	private String nombre;
	private String apellido;
	private String userName;
	private String clave;
	private String clave1;
	private String clave2;
	

	private String mail;
	private String descripcion;
	
	private Rol rol;
	
	private String idOrganizacion;
	
	private boolean foto;
	
	private Date ingreso;
    private Date baja;
    private Date modificacion;
}
