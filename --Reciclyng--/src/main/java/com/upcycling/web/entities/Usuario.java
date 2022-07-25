package com.upcycling.web.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.upcycling.web.enums.Rol;

import lombok.Data;

@Data
@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name = "uuid" , strategy="uuid2")
	private String id;
	private String nombre;
	private String apellido;
	private String mail;
	private String userName;
	private String clave;
	@Enumerated(EnumType.STRING)
	private Rol rol;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Organizacion organizacion;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date ingreso;
	
	@Temporal(TemporalType.TIMESTAMP)
    private Date alta;
    @Temporal(TemporalType.TIMESTAMP)
    private Date baja;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificacion;
	
}
