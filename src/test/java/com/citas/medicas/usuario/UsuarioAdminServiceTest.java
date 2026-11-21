package com.citas.medicas.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.citas.medicas.medico.MedicoRepository;
import com.citas.medicas.paciente.PacienteProvisioningService;
import com.citas.medicas.paciente.PacienteRepository;
import com.citas.medicas.rol.Rol;
import com.citas.medicas.rol.RolRepository;

class UsuarioAdminServiceTest {

	private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
	private final RolRepository rolRepository = mock(RolRepository.class);
	private final MedicoRepository medicoRepository = mock(MedicoRepository.class);
	private final PacienteRepository pacienteRepository = mock(PacienteRepository.class);
	private final PacienteProvisioningService pacienteProvisioningService = mock(PacienteProvisioningService.class);
	private final PasswordEncoder encoder = mock(PasswordEncoder.class);

	private final UsuarioAdminService service = new UsuarioAdminService(
			usuarioRepository, rolRepository, medicoRepository,
			pacienteRepository, pacienteProvisioningService, encoder);

	@BeforeEach
	void prepararRolPaciente() {
		Rol rol = new Rol();
		rol.setId(2L);
		rol.setNombre("PACIENTE");
		when(rolRepository.findByNombre("PACIENTE")).thenReturn(Optional.of(rol));
	}

	@Test
	void rechazaRegistroSinNombreOApellido() {
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> service.guardar(null, "", "Perez", "12345678",
						"ana@gmail.com", null, null, "clave1234", "PACIENTE"));

		assertEquals("Nombre y apellido obligatorios", error.getMessage());
	}

	@Test
	void rechazaRegistroConCorreoInvalido() {
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> service.guardar(null, "Ana", "Perez", "12345678",
						"sin-arroba", null, null, "clave1234", "PACIENTE"));

		assertEquals("Correo inválido", error.getMessage());
	}

	@Test
	void rechazaRegistroConDniInvalido() {
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> service.guardar(null, "Ana", "Perez", "123",
						"ana@gmail.com", null, null, "clave1234", "PACIENTE"));

		assertEquals("El DNI debe tener 8 dígitos", error.getMessage());
	}

	@Test
	void rechazaRegistroConRolInvalido() {
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> service.guardar(null, "Ana", "Perez", "12345678",
						"ana@gmail.com", null, null, "clave1234", "SECRETARIA"));

		assertEquals("Rol inválido", error.getMessage());
	}

	@Test
	void rechazaRegistroConContrasenaCorta() {
		IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
				() -> service.guardar(null, "Ana", "Perez", "12345678",
						"ana@gmail.com", null, null, "123", "PACIENTE"));

		assertEquals("La contraseña debe tener al menos 8 caracteres", error.getMessage());
	}
}
