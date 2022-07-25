package com.upcycling.web.controllers;

import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.PAIS_LABEL;
import static com.upcycling.web.utils.Textos.ORGANIZACION_LABEL;
import static com.upcycling.web.utils.Textos.QUERY_LABEL;
import static com.upcycling.web.utils.Textos.URL_LABEL;

import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.entities.Pais;
import com.upcycling.web.exceptions.OrganizacionException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.OrganizacionModel;
import com.upcycling.web.models.PaisModel;
import com.upcycling.web.services.OrganizacionService;

@Controller
@RequestMapping("/organizacion")
public class OrganizacionController extends Controlador{

	@Autowired
	private OrganizacionService organizacionService;
	
	public OrganizacionController() {
		super("organizacion-list", "organizacion-form");
	}

	// REGISTRAR
	@GetMapping("/registrar")
	public String registrarOrganizacion() {
		return "form-organizacion";
	}

	@PostMapping("/registrar")
	public String registrarOrganizacion(ModelMap modelo, @RequestParam String nombre, @RequestParam String domicilio,
			@RequestParam String mail) throws OrganizacionException {

		try {

			organizacionService.registrarOrganizacion(nombre, domicilio, mail);

			modelo.put("Descripcion", "Su organización fue registrada con exito");

			return "exito";

		} catch (OrganizacionException e) {
			modelo.put("Error: ", e.getMessage());
			modelo.put("domicilio", domicilio);
			modelo.put("mail", mail);

			System.out.println("Error:" + e.getMessage());
			return "form-organizacion";
		}

	}

	// MODIFICAR

	@GetMapping("/modificar/{id}")
	public String modificarOrganizacion(@PathVariable String id, ModelMap modelo) {

		modelo.put("organizacion", organizacionService.getOne(id));
		return "form-organizacion-modif";
	}

	@PostMapping("/modificar/{id}")
	public String modificarOrganizacion(ModelMap modelo, @PathVariable String id, @RequestParam String nombre,
			@RequestParam String domicilio, @RequestParam String mail) throws OrganizacionException {

		try {

			organizacionService.modificarOrganizacion(id, nombre, domicilio, mail);
			modelo.put("Descripcion", "Modificacion exitosa");
			return "form-organizacion-modif";

		} catch (OrganizacionException e) {
			modelo.put("Error:", e.getMessage());
			return "form-organizacion-modif";

		}
	}

	// ELIMINAR
	@PostMapping("/eliminar1")
	public String eliminarCliente(@RequestParam String id) throws OrganizacionException {

		organizacionService.eliminarOrganizacion(id);

		return "usuario-lista";
	}

	// LISTAR
	@GetMapping("/listar")
	public String listarLibros(ModelMap modelo) {

		List<Organizacion> organizaciones = organizacionService.listarTodos();
		modelo.addAttribute("orgs", organizaciones);
		return "organizacion-list";
	}
	
	//--------------------------------------------------------------------//
	@GetMapping("/listado")
	public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
		
		ModelAndView modelo = new ModelAndView(vistaListado);
		ordenar(paginable, modelo);

		Page<Organizacion> page = organizacionService.listarActivos(paginable, q);
		if (q != null && !q.isEmpty()) {
			modelo.addObject(QUERY_LABEL, q);
		}
		modelo.addObject(PAGE_LABEL, page);
		modelo.addObject(URL_LABEL, "/organizacion/listado");
		modelo.addObject(ORGANIZACION_LABEL, new OrganizacionModel());
		
		return modelo;
	}
	
	@GetMapping("/formulario")
	public ModelAndView formulario(@RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
		
		ModelAndView modelo = new ModelAndView(vistaFormulario);
		OrganizacionModel organizacion = new OrganizacionModel();
		
		if (accion == null || accion.isEmpty()) {
			accion = GUARDAR_LABEL;
		}

		if (id != null) {
			organizacion = organizacionService.buscar(id);
		}
		modelo.addObject(ORGANIZACION_LABEL, organizacion);
		modelo.addObject(ACCION_LABEL, accion);
		return modelo;
	}
	
	@PostMapping("/eliminar")
	public String eliminar(@ModelAttribute(ORGANIZACION_LABEL) OrganizacionModel organizacion, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "eliminar");
		try {
			organizacionService.eliminar(organizacion.getId());
			return "redirect:/organizacion/listado";
		} catch (Exception e) {
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar la organización.");
			return vistaFormulario;
		}
	}
	
	@PostMapping("/guardar")
	public String guardar(HttpSession session, @Valid @ModelAttribute(ORGANIZACION_LABEL) OrganizacionModel organizacion, BindingResult resultado, ModelMap modelo) {
		try {
			if (resultado.hasErrors()) {
				error(modelo, resultado);
			} else {
				organizacionService.guardar(organizacion);
				return "redirect:/organizacion/listado";
			}
		} catch (WebException e) {
			modelo.addAttribute(ERROR, "Ocurrió un error al intentar modificar la organización. " + e.getMessage());
		} catch (Exception e) {
			modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar la organización.");
			log.error(ERROR_INESPERADO, e);
		}
		return vistaFormulario;
	}
}
