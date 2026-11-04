package com.citas.medicas.security;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByCorreo(correo)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + correo));

		String nombreRol = usuario.getRol().getNombre();
		List<GrantedAuthority> authorities = Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + nombreRol));

		return new User(
				usuario.getCorreo(),
				usuario.getContraseña(),
				Boolean.TRUE.equals(usuario.getEstado()),
				true,
				true,
				true,
				authorities);
	}
}
