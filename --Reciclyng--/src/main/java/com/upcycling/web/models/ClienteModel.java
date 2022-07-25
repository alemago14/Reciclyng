package com.upcycling.web.models;

import lombok.Data;

@Data
public class ClienteModel {
	private String id;
	private String nombre;
	private String email;
	private String identificador;
	private OrganizacionModel organizacionModel;
}