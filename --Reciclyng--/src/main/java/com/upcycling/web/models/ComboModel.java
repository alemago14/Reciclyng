package com.upcycling.web.models;

import lombok.Data;

@Data
public class ComboModel {
	
	public ComboModel(String id, String valor) {
		this.id = id;
		this.valor = valor;
	}
	
	private String id;
	private String valor;
}
