package com.upcycling.web.models;

import lombok.Data;

@Data
public class DepartamentoModel {
	private String id;
	private String nombre;
	private ProvinciaModel provinciaModel;
}