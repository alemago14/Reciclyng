package com.upcycling.web.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
public class Organizacion {
	
	@Id
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name = "uuid" , strategy="uuid2")
	private String id;
	private String nombre;
	private String domicilio;
	private String mail;
	private String identificador;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date alta;
	@Temporal(TemporalType.TIMESTAMP)
	private Date baja;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modificacion;
	
	
}
