package com.upcycling.web.controllers;


import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.CLIENTE_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.QUERY_LABEL;
import static com.upcycling.web.utils.Textos.URL_LABEL;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import com.upcycling.web.entities.Cliente;
import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.exceptions.ClienteException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.ClienteModel;
import com.upcycling.web.services.ClienteService;
import com.upcycling.web.services.OrganizacionService;

@Controller
@RequestMapping("/cliente")
public class ClienteController extends Controlador{
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private OrganizacionService organizacionService;
	
	public ClienteController() {
		super("cliente-list", "cliente-form");
	}
	
	@GetMapping("/listado-de-clientes")
	public String listarClientes(ModelMap model) {
		model.addAttribute("listadoClientes", clienteService.listarClientes());
		return "clientes/listado-clientes";
	}
	
	@PostMapping("/cargar-cliente")
	public String crearCliente(
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "nombre", required = false) String nombre,
			ModelMap model) throws ClienteException{
		
		try {
			clienteService.crearCliente(nombre, email);
			return "clientes/nuevo-cliente";
		}catch (ClienteException e) {
			model.put("error", e.getMessage());
			return "clientes/nuevo-cliente";
		}
		
	}
	
	@PostMapping("/modificar-cliente")
	public String modificarCliente(
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "nombre", required = false) String nombre,
			@RequestParam(name = "id", required = false) String id,
			ModelMap model) throws ClienteException {
		
		try {
			clienteService.modificarCliente(id, email, nombre);
			return "redirect:/clientes/listado-de-clientes";
		}catch(ClienteException e) {
			model.put("error", e.getMessage());
			return "clientes/modificar-cliente";
		}
	}
	
	
	@PostMapping("/eliminar-cliente")
	public String eliminarCliente(
			@RequestParam(name = "id", required = false) String id) {
		
		if(id == null || id.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		clienteService.eliminarCliente(id);
		
		return "clientes/listado-clientes";
	}
	
	//--------------------------------------------------------------------//
		@GetMapping("/listado")
		public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
			
			ModelAndView modelo = new ModelAndView(vistaListado);
			ordenar(paginable, modelo);

			Page<Cliente> page = clienteService.listarActivos(paginable, q);
			if (q != null && !q.isEmpty()) {
				modelo.addObject(QUERY_LABEL, q);
			}
			modelo.addObject(PAGE_LABEL, page);
			modelo.addObject(URL_LABEL, "/cliente/listado");
			modelo.addObject(CLIENTE_LABEL, new ClienteModel());
			
			return modelo;
		}
		
		@GetMapping("/formulario")
		public ModelAndView formulario(@RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
			
			ModelAndView modelo = new ModelAndView(vistaFormulario);
			ClienteModel cliente = new ClienteModel();
			
			if (accion == null || accion.isEmpty()) {
				accion = GUARDAR_LABEL;
			}
			
			if(isAdministrador()) {
				modelo.addObject("organizaciones", organizacionService.listarActivos());
			}

			if (id != null) {
				cliente = clienteService.buscar(id);
			}
			modelo.addObject(CLIENTE_LABEL, cliente);
			modelo.addObject(ACCION_LABEL, accion);
			return modelo;
		}
		
		@PostMapping("/eliminar")
		public String eliminar(@ModelAttribute(CLIENTE_LABEL) ClienteModel cliente, ModelMap model) {
			model.addAttribute(ACCION_LABEL, "eliminar");
			try {
				clienteService.eliminar(cliente.getId());
				return "redirect:/organizacion/listado";
			} catch (Exception e) {
				model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar el cliente.");
				return vistaFormulario;
			}
		}
		
		@PostMapping("/guardar")
		public String guardar(HttpSession session, @Valid @ModelAttribute(CLIENTE_LABEL) ClienteModel cliente, BindingResult resultado, ModelMap modelo) {
			try {
				if (resultado.hasErrors()) {
					error(modelo, resultado);
				} else {
					if(cliente.getOrganizacionModel() == null || cliente.getOrganizacionModel().getId() == null || cliente.getOrganizacionModel().getId().isEmpty()) {
						Organizacion organizacion = getOrganizacion(session);
						cliente.setOrganizacionModel(organizacionService.buscar(organizacion.getId()));
					}
					clienteService.guardar(cliente);
					return "redirect:/cliente/listado";
				}
			} catch (WebException e) {
				modelo.addAttribute(ERROR, "Ocurrió un error al intentar modificar el cliente. " + e.getMessage());
			} catch (Exception e) {
				modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar el cliente.");
				log.error(ERROR_INESPERADO, e);
			}
			return vistaFormulario;
		}
	

}
