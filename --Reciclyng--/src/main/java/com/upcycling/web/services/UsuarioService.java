package com.upcycling.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.upcycling.web.utils.Textos.USUARIO_LABEL;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.upcycling.web.converters.UsuarioConverter;
import com.upcycling.web.entities.Usuario;
import com.upcycling.web.enums.Rol;
import com.upcycling.web.exceptions.UsuarioException;
import com.upcycling.web.exceptions.WebException;
import com.upcycling.web.models.UsuarioModel;
import com.upcycling.web.repositories.UsuarioRepository;
import com.upcycling.web.validations.UsuarioValidation;

@Service("usuarioService")
public class UsuarioService implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioConverter usuarioConverter;
	
	@Autowired
	private UsuarioValidation usuarioValidation;

	public static final String NOT_FOUND = "No se encontró el usuario";

	// REGISTRAR
	public void registrarUsuario(String nombre, String apellido, String mail, Rol rol) throws UsuarioException {

		usuarioValidation.validar(nombre, apellido, mail, rol);

		Usuario usuario = new Usuario();

		usuario.setNombre(nombre);
		usuario.setApellido(apellido);
		usuario.setMail(mail);
		usuario.setRol(rol);

		usuarioRepository.save(usuario);
	}

	// MODIFICAR
	public void modificarUsuario(String id, String nombre, String apellido, String mail, Rol rol)
			throws UsuarioException {

		usuarioValidation.validar(nombre, apellido, mail, rol);

		Optional<Usuario> respuesta = usuarioRepository.findById(id);
		if (respuesta.isPresent()) {
			Usuario usuario = respuesta.get();
			usuario.setNombre(nombre);
			usuario.setApellido(apellido);
			usuario.setMail(mail);
			usuario.setRol(rol);

			usuarioRepository.save(usuario);
		} else {
			throw new UsuarioException(NOT_FOUND);
		}

	}

	// ELIMINAR
	public void eliminarUsuario(String id) throws UsuarioException {

		usuarioValidation.validarId(id);

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		if (usuario.isPresent()) {

			usuarioRepository.delete(usuario.get());

		} else {
			throw new UsuarioException(NOT_FOUND);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={WebException.class, Exception.class})
	public Usuario guardar(UsuarioModel usuarioModel) throws WebException{
		Usuario usuario = usuarioConverter.modeloToEntidad(usuarioModel);
	    
		validarUsuario(usuarioModel, usuario);
		
		usuario.setModificacion(new Date());
		
		if(usuarioModel.getClave() != null && !usuarioModel.getClave().isEmpty()) {
			usuario.setClave(new BCryptPasswordEncoder().encode(usuarioModel.getClave()));
		}
		
		usuario = usuarioRepository.save(usuario);
		
		return usuario;
	}
	
	public void eliminar(String id) throws WebException{
		if(id != null) {
			Usuario usuario = usuarioRepository.buscarPorId(id);
			if(usuario == null) {
				throw new WebException("El identificador no se encuentra asociado a ningún usuario.");
			}
			usuario.setBaja(new Date());
			usuarioRepository.save(usuario);
		} else {
			throw new WebException("El identificador del usuario no puede ser nulo.");
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={WebException.class, Exception.class})
	public Usuario cambiarClave(UsuarioModel model) throws WebException{
		
		if (model.getClave() == null || model.getClave().isEmpty()) {
			throw new WebException("La clave del usuario no puede ser nula o vacía.");
		}
		
		if (model.getClave1() == null || model.getClave1().isEmpty()) {
			throw new WebException("La clave del usuario no puede ser nula o vacía.");
		}
		
		if (model.getClave2() == null || model.getClave2().isEmpty()) {
			throw new WebException("La clave del usuario no puede ser nula o vacía.");
		}
		
		Usuario usuario = usuarioRepository.buscarPorId(model.getId());
		if(model.getClave1().equals(model.getClave2())){
			usuario.setClave(new BCryptPasswordEncoder().encode(model.getClave1()));
		} else {
			throw new WebException("Error al blanquear la contraseña, las claves ingresadas no coinciden");
		}
		return usuarioRepository.save(usuario);
	}
	
	private void validarUsuario(UsuarioModel usuarioModel, Usuario usuario) throws WebException {
		
		if(usuarioModel.getClave() == null || usuarioModel.getClave().isEmpty()) throw new WebException("La clave del usuario no puede ser nula o vacía.");
		
		if(usuario.getUserName() == null || usuario.getUserName().isEmpty()) throw new WebException("El usuario no puede ser nulo o vacío.");
		
		if(usuario.getClave() == null || usuario.getClave().isEmpty()) throw new WebException("La clave del usuario no puede ser nula o vacía.");
		
		
		if (usuario.getOrganizacion() == null) throw new WebException("El usuario debe estar vinculada a una organización.");
		
		
		if (usuarioModel.getId() == null || usuarioModel.getId().isEmpty()) {
			if (usuarioRepository.buscarPorUsuario(usuarioModel.getUserName()) != null) throw new WebException("Ya existe un usuario con ese nombre de usuario.");
			if (usuarioRepository.buscarPorMail(usuarioModel.getMail()) != null) throw new WebException("Ya existe un usuario con ese mail.");
		}
	}
	
	public UserDetails loadUserByUsername(String username) {
		Usuario usuario = findByUsername(username);
		
		if(usuario != null){
		    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		    HttpSession session = attr.getRequest().getSession(true); 
		    session.setAttribute(USUARIO_LABEL, usuario);	    	
	    	return transformar(usuario);
		} else {
			throw new UsernameNotFoundException("Nombre de usuario o contraseña incorrecta.");
		}
		
	}
	
	public UsuarioModel buscarModel(String id) {
		return usuarioConverter.entidadToModelo(usuarioRepository.buscarPorId(id));
	}
	
	private User transformar(Usuario usuario){
		List<GrantedAuthority> permisos = new ArrayList<>();
		if(usuario != null){
			
			GrantedAuthority permiso = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
			permisos.add(permiso);
			
			usuario.setIngreso(new Date());
			
			usuario = usuarioRepository.save(usuario);
			
			return new User(usuario.getUserName(), usuario.getClave(), usuario.getBaja() == null, true, true, true, permisos);
		} else {
			return null;
		}
	}
	
	public Usuario findByUsername(String username) {
		return usuarioRepository.buscarPorUsuario(username);
	}

	// LISTAR

	public List<Usuario> listarTodos() {

		return usuarioRepository.findAll();
	}
	
	public Page<Usuario> buscarTodos(Pageable paginable, String q, String idOrganizacion){
		if(q == null || q.isEmpty()) {
			return usuarioRepository.listarActivos(paginable, idOrganizacion);
		} else {
			return usuarioRepository.listarActivos(paginable, "%" + q + "%", idOrganizacion);
		}
	}
	
	public Page<Usuario> buscarTodosAdmin(Pageable paginable, String q){
		if(q == null || q.isEmpty()) {
			return usuarioRepository.listarActivosAdmin(paginable);
		} else {
			return usuarioRepository.listarActivosAdmin(paginable, "%" + q + "%");
		}
	}

	// funciones

	@SuppressWarnings("deprecation")
	public Usuario getOne(String id) {
		return usuarioRepository.getOne(id);
	}
}
