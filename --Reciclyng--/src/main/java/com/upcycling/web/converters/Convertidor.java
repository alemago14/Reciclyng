package com.upcycling.web.converters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.upcycling.web.exceptions.WebException;


public abstract class Convertidor<M extends Object, E extends Object> {
	public abstract E modeloToEntidad(M m) throws WebException;

	public abstract M entidadToModelo(E e);

	protected Log log;

	protected Convertidor() {
		this.log = LogFactory.getLog(getClass());
	}

}
