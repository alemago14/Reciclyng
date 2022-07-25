package com.upcycling.web.controllers;

import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.PAIS_LABEL;
import static com.upcycling.web.utils.Textos.QUERY_LABEL;
import static com.upcycling.web.utils.Textos.URL_LABEL;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.upcycling.web.entities.Pais;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.PaisModel;
import com.upcycling.web.services.PaisService;

@Controller
@RequestMapping("/pais")
public class PaisController extends Controlador {

	@Autowired
	private PaisService paisService;

	public PaisController() {
		super("pais-list", "pais-form");
	}

	@PostMapping("/guardar")
	public String guardar(HttpSession session, @Valid @ModelAttribute(PAIS_LABEL) PaisModel pais, BindingResult resultado, ModelMap modelo) {
		try {
			if (resultado.hasErrors()) {
				error(modelo, resultado);
			} else {
				paisService.guardar(pais);
				return "redirect:/pais/listado";
			}
		} catch (WebException e) {
			modelo.addAttribute(ERROR, "Ocurrió un error al intentar modificar el país. " + e.getMessage());
		} catch (Exception e) {
			modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar el país.");
			log.error(ERROR_INESPERADO, e);
		}
		return vistaFormulario;
	}

	@PostMapping("/eliminar")
	public String eliminar(@ModelAttribute(PAIS_LABEL) PaisModel pais, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "eliminar");
		try {
			paisService.eliminar(pais.getId());
			return "redirect:/pais/listado";
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar el país.");
			return vistaFormulario;
		}
	}

	@GetMapping("/formulario")
	public ModelAndView formulario(@RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
		
		ModelAndView modelo = new ModelAndView(vistaFormulario);
		PaisModel pais = new PaisModel();
		
		if (accion == null || accion.isEmpty()) {
			accion = GUARDAR_LABEL;
		}

		if (id != null) {
			pais = paisService.buscar(id);
		}
		modelo.addObject(PAIS_LABEL, pais);
		modelo.addObject(ACCION_LABEL, accion);
		return modelo;
	}

	@GetMapping("/listado")
	public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
		
		ModelAndView modelo = new ModelAndView(vistaListado);
		ordenar(paginable, modelo);

		Page<Pais> page = paisService.listarActivos(paginable, q);
		if (q != null && !q.isEmpty()) {
			modelo.addObject(QUERY_LABEL, q);
		}
		modelo.addObject(PAGE_LABEL, page);
		modelo.addObject(URL_LABEL, "/pais/listado");
		modelo.addObject(PAIS_LABEL, new PaisModel());
		
		return modelo;
	}

}