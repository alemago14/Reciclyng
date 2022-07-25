package com.upcycling.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.upcycling.web.utils.Textos.LOGOUT_LABEL;
import static com.upcycling.web.utils.Textos.USUARIO_LABEL;
import static com.upcycling.web.utils.Textos.ERROR;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.upcycling.web.entities.Usuario;
import com.upcycling.web.enums.Rol;
import com.upcycling.web.services.UsuarioService;


@Controller
public class LoginController {

	@Autowired
	@Qualifier("usuarioService")
	private UsuarioService usuarioService;

	@GetMapping("/index")
	public String login(Model model) {
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model, @RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
		if (error != null && !error.equals("")) {
			error = "Nombre de usuario o contraseña incorrecta.";
		}
		model.addAttribute(ERROR, error);
		model.addAttribute(LOGOUT_LABEL, logout);

		return "login";
	}

	@GetMapping({ "/loginsuccess", "/" })
	public String logincheck(HttpSession session) {
		return getRedirect(session);
	}
	
	public String getRedirect(HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute(USUARIO_LABEL);
			if (usuario != null) {
				
				Rol rol = usuario.getRol();
				if (rol.equals(Rol.ADMINISTRADOR)) {
					session.setAttribute("ROL", "ADMINISTRADOR");
					return "redirect:/pais/listado?sort=nombre,desc";
				} else if (rol.equals(Rol.PARTNER)) {
					session.setAttribute("ROL", "PARTNER");
					return "redirect:/pais/listado?sort=nombre,desc";
				} else if (rol.equals(Rol.CLIENTE)) {
					session.setAttribute("ROL", "CLIENTE");
					return "redirect:/pais/listado?sort=nombre,desc";
				} else {
					session.setAttribute("ROL", "USER");
					return "redirect:/pais/listado?sort=nombre,desc";
				} 
				
			}
		return "redirect:/login";
	}

	@GetMapping(value = "/logout")
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

}
