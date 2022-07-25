package com.upcycling.web.controllers;

import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.PAISES_LABEL;
import static com.upcycling.web.utils.Textos.PROVINCIA_LABEL;
import static com.upcycling.web.utils.Textos.QUERY_LABEL;
import static com.upcycling.web.utils.Textos.URL_LABEL;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.upcycling.web.entities.Pais;
import com.upcycling.web.entities.Provincia;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.ProvinciaModel;
import com.upcycling.web.services.PaisService;
import com.upcycling.web.services.ProvinciaService;

@Controller
@PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
@RequestMapping("/provincia")
public class ProvinciaController extends Controlador {

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	private PaisService paisService;

	public ProvinciaController() {
		super("provincia-list", "provincia-form");
	}

	private void llenarCombo(ModelMap modelo) {
		List<Pais> paises = paisService.listarActivos();
		modelo.addAttribute(PAISES_LABEL, paises);
	}

	@PostMapping("/guardar")
	public String guardar(HttpSession session, @Valid @ModelAttribute(PROVINCIA_LABEL) ProvinciaModel provincia, BindingResult resultado, ModelMap modelo) {
		try {
			if (resultado.hasErrors()) {
				error(modelo, resultado);
			} else {
				provinciaService.guardar(provincia);
				return "redirect:/provincia/listado";
			}
		} catch (WebException e) {
			llenarCombo(modelo);
			modelo.addAttribute(ERROR, "Ocurrió un error al intentar modificar la provincia. " + e.getMessage());
		} catch (Exception e) {
			llenarCombo(modelo);
			modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar la provincia.");
			log.error(ERROR_INESPERADO, e);
		}
		return vistaFormulario;
	}

	@PostMapping("/eliminar")
	public String eliminar(@ModelAttribute(PROVINCIA_LABEL) ProvinciaModel provincia, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "eliminar");
		try {
			provinciaService.eliminar(provincia.getId());
			return "redirect:/provincia/listado";
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar la provincia.");
			return vistaFormulario;
		}
	}

	@GetMapping("/formulario")
	public ModelAndView formulario(HttpSession session, @RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
		ModelAndView modelo = new ModelAndView(vistaFormulario);
		ProvinciaModel provincia = new ProvinciaModel();
		if (accion == null || accion.isEmpty()) {
			accion = GUARDAR_LABEL;
		}

		if (id != null) {
			provincia = provinciaService.buscar(id);
		}

		llenarCombo(modelo.getModelMap());

		
		modelo.addObject(PROVINCIA_LABEL, provincia);
		modelo.addObject(ACCION_LABEL, accion);
		return modelo;
	}

	@GetMapping("/listado")
	public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {

		ModelAndView modelo = new ModelAndView(vistaListado);
		ordenar(paginable, modelo);
		Page<Provincia> page = provinciaService.listarActivos(paginable, q);
		if (q != null && !q.isEmpty()) {
			modelo.addObject(QUERY_LABEL, q);
		}
		modelo.addObject(PAGE_LABEL, page);
		modelo.addObject(URL_LABEL, "/provincia/listado");
		modelo.addObject(PROVINCIA_LABEL, new ProvinciaModel());

		return modelo;
	}
	
	@GetMapping("/listado/{id}")
	public @ResponseBody ResponseEntity<List<ComboModel>> provincias(@PathVariable String id, HttpSession session) {
		List<ComboModel> listado = provinciaService.listarActivos(id);
		return new ResponseEntity<>(listado, HttpStatus.OK);
	}

}
