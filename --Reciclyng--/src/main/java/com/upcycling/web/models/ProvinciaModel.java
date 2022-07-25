package com.upcycling.web.models;

import lombok.Data;

@Data
public class ProvinciaModel {
	private String id;
	private String nombre;
	private PaisModel paisModel;
}