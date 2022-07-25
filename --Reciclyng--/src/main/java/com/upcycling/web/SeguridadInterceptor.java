package com.upcycling.web;

import static com.upcycling.web.utils.Textos.USUARIO_LABEL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.upcycling.web.entities.Usuario;
import com.upcycling.web.enums.Rol;
import com.upcycling.web.services.UsuarioService;


@Component
public class SeguridadInterceptor implements HandlerInterceptor {

	private static final String ROL = "ROL";
	
	
	@Autowired
	private UsuarioService usuarioService;

	
	private void aplicarRol(Usuario usuario, HttpSession session) {
		if (usuario != null) {
			Rol rol = usuario.getRol();
			if (rol.equals(Rol.ADMINISTRADOR)) {
				session.setAttribute(ROL, "ADMINISTRADOR");
			} else if (rol.equals(Rol.PARTNER)) {
				session.setAttribute(ROL, "PARTNER");
			} else if (rol.equals(Rol.CLIENTE)) {
				session.setAttribute(ROL, "CLIENTE");
			} else if (rol.equals(Rol.USER)) {
				session.setAttribute(ROL, "USER");
			}
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		if (session.getAttribute(USUARIO_LABEL) == null) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			if (auth != null && auth.isAuthenticated() && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
				Usuario usuario = usuarioService.findByUsername(auth.getName());
				session.setAttribute(USUARIO_LABEL, usuario);
				aplicarRol(usuario, session);
			}
		} else {
			Usuario usuario = (Usuario) session.getAttribute(USUARIO_LABEL);
			if(session.getAttribute(ROL) == null) {
				aplicarRol(usuario, session);
			}
			
		}
		
		return preHandle(request, response, handler);
	}
	
	
}
