package com.citas.medicas.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.citas.medicas.security.jwt.JwtTokenProvider;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private JwtTokenProvider tokenProvider;

	@MockitoBean
	private UsuarioRepository usuarioRepository;

	@MockitoBean
	private RegistroService registroService;

	@MockitoBean
	private VerificacionRegistroService verificacionRegistroService;

	@Test
	void loginCorrectoDevuelveToken() throws Exception {
		Authentication auth = new UsernamePasswordAuthenticationToken(
				"ana@gmail.com", null,
				List.of(new SimpleGrantedAuthority("ROLE_PACIENTE")));

		Usuario usuario = new Usuario();
		usuario.setIdUsuario(1L);
		usuario.setNombre("Ana");
		usuario.setApellido("Perez");

		when(authenticationManager.authenticate(any())).thenReturn(auth);
		when(usuarioRepository.findByCorreo("ana@gmail.com")).thenReturn(Optional.of(usuario));
		when(tokenProvider.generarToken("ana@gmail.com", 1L, "PACIENTE")).thenReturn("token-jwt");
		when(tokenProvider.expiracionSegundos()).thenReturn(7200L);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo\":\"ana@gmail.com\",\"password\":\"clave1234\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("token-jwt"))
			.andExpect(jsonPath("$.rol").value("PACIENTE"))
			.andExpect(jsonPath("$.nombre").value("Ana"));
	}

	@Test
	void loginConCredencialesIncorrectasDevuelve401() throws Exception {
		when(authenticationManager.authenticate(any()))
				.thenThrow(new BadCredentialsException("Credenciales inválidas"));

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"correo\":\"ana@gmail.com\",\"password\":\"incorrecta\"}"))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.error").value("Credenciales incorrectas o cuenta inactiva."));
	}
}
