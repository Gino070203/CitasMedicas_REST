package com.citas.medicas.security;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.citas.medicas.paciente.PacienteProvisioningService;
import com.citas.medicas.rol.Rol;
import com.citas.medicas.rol.RolRepository;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class OAuth2UsuarioService extends DefaultOAuth2UserService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RolRepository rolRepository;

	@Autowired
	private PacienteProvisioningService pacienteProvisioningService;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String correo = oAuth2User.getAttribute("email");
		String nombre = oAuth2User.getAttribute("given_name");
		String apellido = oAuth2User.getAttribute("family_name");

		Usuario usuario = usuarioRepository.findByCorreo(correo).orElseGet(() -> {
			Rol rolPaciente = rolRepository.findByNombre("PACIENTE")
					.orElseGet(() -> {
						Rol nuevoRol = new Rol();
						nuevoRol.setNombre("PACIENTE");
						return rolRepository.save(nuevoRol);
					});

			Usuario nuevoUsuario = new Usuario();
			nuevoUsuario.setCorreo(correo);
			nuevoUsuario.setNombre(nombre != null ? nombre : "Usuario");
			nuevoUsuario.setApellido(apellido != null ? apellido : "Google");
			nuevoUsuario.setContraseña("");
			nuevoUsuario.setEstado(true);
			nuevoUsuario.setRol(rolPaciente);
			return usuarioRepository.save(nuevoUsuario);
		});

		if (usuario.getRol() != null
				&& "PACIENTE".equalsIgnoreCase(usuario.getRol().getNombre())) {
			pacienteProvisioningService.asegurarPerfilPaciente(usuario.getIdUsuario());
		}

		String nombreRol = usuario.getRol().getNombre();
		List<GrantedAuthority> authorities = Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + nombreRol));

		return new DefaultOAuth2User(
				authorities,
				oAuth2User.getAttributes(),
				"email");
	}

}
