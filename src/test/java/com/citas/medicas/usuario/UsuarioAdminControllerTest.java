package com.citas.medicas.usuario;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.citas.medicas.rol.Rol;
import com.citas.medicas.security.jwt.JwtTokenProvider;

@WebMvcTest(UsuarioAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioAdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UsuarioAdminService service;

	@MockitoBean
	private JwtTokenProvider tokenProvider;

	@Test
	void listarDevuelveUsuarios() throws Exception {
		Usuario usuario = usuarioDePrueba();

		when(service.filtrar(any(), any(), any())).thenReturn(List.of(usuario));

		mockMvc.perform(get("/api/admin/usuarios"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].nombre").value("Ana"))
			.andExpect(jsonPath("$[0].rolNombre").value("PACIENTE"))
			.andExpect(jsonPath("$[0].estado").value(true));
	}

	@Test
	void crearUsuarioDevuelve201() throws Exception {
		Usuario usuario = usuarioDePrueba();

		when(service.guardar(any(), any(), any(), any(), any(), any(), any(), any(), any()))
				.thenReturn(usuario);

		mockMvc.perform(post("/api/admin/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Ana\",\"apellido\":\"Perez\",\"dni\":\"12345678\","
						+ "\"correo\":\"ana@gmail.com\",\"password\":\"clave1234\",\"rol\":\"PACIENTE\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.nombre").value("Ana"))
			.andExpect(jsonPath("$.rolNombre").value("PACIENTE"));
	}

	private Usuario usuarioDePrueba() {
		Rol rol = new Rol();
		rol.setNombre("PACIENTE");

		Usuario usuario = new Usuario();
		usuario.setIdUsuario(1L);
		usuario.setNombre("Ana");
		usuario.setApellido("Perez");
		usuario.setDni("12345678");
		usuario.setCorreo("ana@gmail.com");
		usuario.setEstado(true);
		usuario.setRol(rol);
		return usuario;
	}
}
