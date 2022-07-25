package com.upcycling.web.controllers;

import java.util.List;

import static com.upcycling.web.utils.Textos.ORGANIZACIONES_LABEL;
import static com.upcycling.web.utils.Textos.EXITO_LABEL;
import static com.upcycling.web.utils.Textos.ACCION_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;
import static com.upcycling.web.utils.Textos.ERROR_INESPERADO;
import static com.upcycling.web.utils.Textos.GUARDAR_LABEL;
import static com.upcycling.web.utils.Textos.PAGE_LABEL;
import static com.upcycling.web.utils.Textos.USUARIO_LABEL;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.upcycling.web.entities.Organizacion;
import com.upcycling.web.entities.Usuario;
import com.upcycling.web.enums.Rol;
import com.upcycling.web.exceptions.UsuarioException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.UsuarioModel;
import com.upcycling.web.services.OrganizacionService;
import com.upcycling.web.services.UsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController extends Controlador{

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private OrganizacionService organizacionService;
	
	public UsuarioController() {
		super("usuario-list", "usuario-form");
	}

	// REGISTRAR

	@GetMapping("/registrar")
	public String registrarUsuario() {
		return "form-usuario";
	}

	@PostMapping("/registrar")
	public String registrarUsuario(ModelMap modelo, @RequestParam String nombre, @RequestParam String apellido,
			@RequestParam String mail, @RequestParam Rol rol) throws UsuarioException {

		try {

			usuarioService.registrarUsuario(nombre, apellido, mail, rol);
			modelo.put("Descripcion", "El usuario fue registrado con exito");

			return "exito/usuarios";
		} catch (UsuarioException e) {
			modelo.put("Error: ", e.getMessage());
			modelo.put("nombre", nombre);
			modelo.put("apellido", apellido);
			modelo.put("mail", mail);
			modelo.put("rol", rol);

			return "form-usuario";
		}
	}

	// MODIFICAR

	@GetMapping("/modificar/{id}")
	public String modificarUsuario(@PathVariable String id, ModelMap modelo) {

		modelo.put("usuario", usuarioService.getOne(id));
		return "form-usuario-modif";
	}

	@PostMapping("/modificar/{id}")
	public String modificarOrganizacion(ModelMap modelo, @PathVariable String id, @RequestParam String nombre,
			@RequestParam String apellido, @RequestParam String mail, Rol rol) throws UsuarioException {

		try {

			usuarioService.modificarUsuario(id, nombre, apellido, mail, rol);
			modelo.put("Descripcion", "Modificacion exitosa");
			return "form-usuario-modif";

		} catch (UsuarioException e) {
			modelo.put("Error:", e.getMessage());
			return "form-usuario-modif";

		}
	}

	// ELIMINAR
	@PostMapping("/eliminar1")
	public String eliminarCliente(@RequestParam String id) throws UsuarioException {
		usuarioService.eliminarUsuario(id);

		return "usuario-lista";
	}

	// LISTAR
	@GetMapping("/listar")
	public String listarLibros(ModelMap modelo) {

		List<Usuario> usuarios = usuarioService.listarTodos();
		modelo.addAttribute("usuarios", usuarios);
		return "usuario-lista";
	}
	
	
	// ----------------------------------------------------------------------//
	
	private void llenarCombo(ModelMap modelo) {
		List<Organizacion> organizaciones = organizacionService.listarActivos();
		modelo.addAttribute(ORGANIZACIONES_LABEL, organizaciones);
		modelo.addAttribute("roles", Rol.values());
	}
	
	@GetMapping("/listado")
	public ModelAndView listar(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
		ModelAndView modelo = new ModelAndView(vistaListado);

		ordenar(paginable, modelo);
		
		Organizacion organizacion = getOrganizacion(session);
		Page<Usuario> page ;
		if(organizacion != null) {
			page = usuarioService.buscarTodos(paginable, q, organizacion.getId());
		}else {
			page = usuarioService.buscarTodosAdmin(paginable, q);
		}
		
		modelo.addObject(QUERY_LABEL, q);

		modelo.addObject(URL_LABEL, "/usuario/listado");
		modelo.addObject(PAGE_LABEL, page);
		modelo.addObject(USUARIO_LABEL, new UsuarioModel());
		return modelo;
	}
	
	@PostMapping("/guardar")
	public String guardar(HttpSession session, @Valid @ModelAttribute(USUARIO_LABEL) UsuarioModel usuario, BindingResult resultado, ModelMap modelo) {
		try {
			if (resultado.hasErrors()) {
				error(modelo, resultado);
			} else {
				if(usuario.getIdOrganizacion() == null || usuario.getIdOrganizacion().isEmpty()) {
					Organizacion organizacion = getOrganizacion(session);
					usuario.setIdOrganizacion(organizacion.getId());		
				}
				usuarioService.guardar(usuario);
				return "redirect:/usuario/listado";
			}
		} catch (WebException e) {
			llenarCombo(modelo);
			modelo.addAttribute(USUARIO_LABEL, usuario);
			modelo.addAttribute(ERROR,e.getMessage());
		} catch (Exception e) {
			llenarCombo(modelo);
			modelo.addAttribute(USUARIO_LABEL, usuario);
			modelo.addAttribute(ERROR, "Ocurrió un error inesperado al intentar modificar el usuario.");
			log.error(ERROR_INESPERADO, e);
		}
		return vistaFormulario;
	}

	@PostMapping("/eliminar")
	public String eliminar(HttpSession session, @ModelAttribute(USUARIO_LABEL) UsuarioModel usuario, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "eliminar");
		usuario = usuarioService.buscarModel(usuario.getId());
		try {
			
			Organizacion organizacion = getOrganizacion(session);
			if(usuario.getIdOrganizacion() != null && !usuario.getIdOrganizacion().isEmpty() && usuario.getIdOrganizacion().equals(organizacion.getId())){
				usuarioService.eliminar(usuario.getId());
				return "redirect:/usuario/listado";
			}
			
		} catch (WebException e) {
			model.addAttribute("usuario", usuario);
			model.addAttribute(ERROR, "Ocurrió un error al intentar eliminar el usuario. " + e.getMessage());
			return vistaFormulario;
		} catch (Exception e) {
			model.addAttribute("usuario", usuario);
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar eliminar el usuario.");
			return vistaFormulario;
		}
	
		return "redirect:/usuario/listado";
	}
	
	@GetMapping("/formulario")
	public ModelAndView formulario(HttpSession session, @RequestParam(required = false) String id, @RequestParam(required = false) String accion) {
		ModelAndView modelo = new ModelAndView(vistaFormulario);
		UsuarioModel usuario = new UsuarioModel();
		if (accion == null || accion.isEmpty()) {
			accion = GUARDAR_LABEL;
		}

		if (id != null) {
			usuario = usuarioService.buscarModel(id);
		}
		
		modelo.addObject(USUARIO_LABEL, usuario);
		modelo.addObject(ACCION_LABEL, accion);
		llenarCombo(modelo.getModelMap());
		return modelo;
	}
	
	@GetMapping("/perfil")
	public ModelAndView perfil(HttpSession session, Pageable paginable, @RequestParam(required = false) String q) {
		ModelAndView modelo = new ModelAndView("usuario-perfil");

		ordenar(paginable, modelo);

		modelo.addObject(QUERY_LABEL, q);

		modelo.addObject(URL_LABEL, "/usuario/perfil");
		return modelo;
	}
	
	@PostMapping("/blanquear")
	public String cambiarClave(@ModelAttribute(USUARIO_LABEL) UsuarioModel usuario, ModelMap model) {
		model.addAttribute(ACCION_LABEL, "actualizar");
		try {
			usuarioService.cambiarClave(usuario);
			usuario = usuarioService.buscarModel(usuario.getId());
			model.addAttribute(USUARIO_LABEL, usuario);
			model.addAttribute(EXITO_LABEL, "La contraseña se actualizó correctamente. ");
			return vistaFormulario;
		} catch (WebException e) {
			usuario = usuarioService.buscarModel(usuario.getId());
			model.addAttribute(USUARIO_LABEL, usuario);
			model.addAttribute(ERROR, "Ocurrió un error al intentar actualizar la contraseña del usuario. " + e.getMessage());
			return vistaFormulario;
		} catch (Exception e) {
			usuario = usuarioService.buscarModel(usuario.getId());
			model.addAttribute(USUARIO_LABEL, usuario);
			model.addAttribute(ERROR, "Ocurrió un error inesperado al intentar actualizar la contraseña del usuario.");
			return vistaFormulario;
		}
	}

}
