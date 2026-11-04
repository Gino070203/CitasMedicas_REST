package com.citas.medicas.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.citas.medicas.security.jwt.JwtTokenProvider;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2JwtSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenProvider tokenProvider;
	private final UsuarioRepository usuarioRepository;

	@Value("${app.oauth2.redirect-uri:http://localhost:4200/auth/oauth2-redirect}")
	private String redirectUri;

	public OAuth2JwtSuccessHandler(JwtTokenProvider tokenProvider,
								   UsuarioRepository usuarioRepository) {
		this.tokenProvider = tokenProvider;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication) throws IOException {

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String correo = oAuth2User.getAttribute("email");

		Usuario usuario = usuarioRepository.buscarPorCorreoConRol(correo)
				.orElseThrow(() -> new IllegalStateException("No se pudo encontrar la cuenta de Google."));

		String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : "PACIENTE";
		String token = tokenProvider.generarToken(correo, usuario.getIdUsuario(), rol);

		String target = UriComponentsBuilder.fromUriString(redirectUri)
				.queryParam("token", token)
				.queryParam("correo", correo)
				.queryParam("rol", rol)
				.queryParam("nombre", usuario.getNombre())
				.queryParam("apellido", usuario.getApellido())
				.build()
				.toUriString();

		response.sendRedirect(target);
	}
}
