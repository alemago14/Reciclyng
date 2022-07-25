package com.upcycling.web.controllers;

import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.DEPARTAMENTO_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.PAISES_LABEL;
import static com.upcycling.web.utils.Textos.PROVINCIAS_LABEL;
import static com.upcycling.web.utils.Textos.QUERY_LABEL;
import static com.upcycling.web.utils.Textos.URL_LABEL;

import java.util.ArrayList;
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

import com.upcycling.web.entities.Departamento;
import com.upcycling.web.entities.Pais;
import com.upcycling.web.entities.Provincia;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ComboModel;
import com.upcycling.web.models.DepartamentoModel;
import com.upcycling.web.services.DepartamentoService;
import com.upcycling.web.services.PaisService;
import com.upcycling.web.services.ProvinciaService;

@Controller
@PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
@RequestMapping("/departamento")
public class DepartamentoController extends Controlador {

	@Autowired
	private DepartamentoService departamentoService;
	
	@Autowired
	private PaisService paisService;
	
	@Autowired
	private ProvinciaService provinciaService;

	public DepartamentoController() {
		super("departamento-list", "departamento-form");
	}
	
	private void llenarCombos(ModelMap modelo, String idProvincia) {
		List<Pais> paises = paisService.listarActivos();
		modelo.addAttribute(PAISES_LABEL, paises);
		
		if(idProvincia != null) {
			Provincia provincia = provinciaService.buscarPorId(idProvincia);
			modelo.addAttribute(PROVINCIAS_LABEL, provincia);
		} else {
			modelo.addAttribute(PROVINCIAS_LABEL, new ArrayList<>());			
		}
	}
	
	
	@PostMapping("/guardar")
	public String guardar(HttpSession session, @Valid @ModelAttribute(DEPARTAMENTO_LABEL) DepartamentoModel departamento, BindingResult resultado, ModelMap modelo) {
		try {
			if (resultado.hasErrors()) {
				error(modelo, resultado);
			} else {
				departamentoService.guardar(departamento);
				return "redirect:/departamento/listado";
			}
		} catch (WebException e) {
			llenarCombos(modelo, departamento.getProvinciaModel().getId());
			modelo.addAttribute(ERROR, "Ocurrió un error al intentar modificar el departamento. " + e.getMessage());
		} catch (Exception e) {
			llenarCombos(modelo, departamento.getProvinciaModel().getId());
			modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar el departamento.");
			log.error(ERROR_INESPERADO, e);
		}
		return vistaFormulario;
	}

	@PostMapping("/eliminar")
	public String eliminar(@ModelAttribute(DEPARTAMENTO_LABEL) DepartamentoModel departamento, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "eliminar");
		try {
			departamentoService.eliminar(departamento.getId());
			return "redirect:/departamento/listado";
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar el departamento.");
			return vistaFormulario;
		}
	}

	@GetMapping("/formulario")
	public ModelAndView formulario(HttpSession session, @RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
		
		ModelAndView modelo = new ModelAndView(vistaFormulario);
		DepartamentoModel departamento = new DepartamentoModel();
		if (accion == null || accion.isEmpty()) {
			accion = GUARDAR_LABEL;
		}

		if (id != null) {
			departamento = departamentoService.buscar(id);
		}
		if(departamento.getProvinciaModel() != null) {
			llenarCombos(modelo.getModelMap(), departamento.getProvinciaModel().getId());
		}else {
			llenarCombos(modelo.getModelMap(), null);
		}
		modelo.addObject(DEPARTAMENTO_LABEL, departamento);
		modelo.addObject(ACCION_LABEL, accion);
		return modelo;
		
	}

	@GetMapping("/listado")
	public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
		
		ModelAndView modelo = new ModelAndView(vistaListado);
		ordenar(paginable, modelo);
		Page<Departamento> page = departamentoService.listarActivos(paginable, q);
		if (q != null && !q.isEmpty()) {
			modelo.addObject(QUERY_LABEL, q);
		}
		modelo.addObject(PAGE_LABEL, page);
		modelo.addObject(URL_LABEL, "/departamento/listado");
		modelo.addObject(DEPARTAMENTO_LABEL, new DepartamentoModel());
		
		return modelo;
	}
	
	@GetMapping("/listado/{id}")
	public @ResponseBody ResponseEntity<List<ComboModel>> departamentos(@PathVariable String id) {
		 List<ComboModel> listado = departamentoService.listarActivosPorProvincia(id);
		 return new ResponseEntity<>(listado,  HttpStatus.OK);
	}

}
