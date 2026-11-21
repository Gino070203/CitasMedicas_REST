package com.citas.medicas.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.citas.medicas.rol.Rol;
import com.citas.medicas.rol.RolRepository;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

/**
 * Crea los roles base (ADMIN, PACIENTE, MEDICO) y un usuario administrador
 * inicial si no existen. Idempotente: se ejecuta en cada arranque sin duplicar.
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private final RolRepository rolRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.correo:admin123@gmail.com}")
	private String adminCorreo;

	@Value("${app.admin.password:admin123}")
	private String adminPassword;

	public DataInitializer(RolRepository rolRepository,
						   UsuarioRepository usuarioRepository,
						   PasswordEncoder passwordEncoder) {
		this.rolRepository = rolRepository;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(String... args) {
		crearRolSiNoExiste("ADMIN");
		crearRolSiNoExiste("PACIENTE");
		crearRolSiNoExiste("MEDICO");

		String correoLimpio = adminCorreo.trim().toLowerCase(Locale.ROOT);

		if (usuarioRepository.findByCorreo(correoLimpio).isEmpty()) {
			Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseThrow();

			Usuario admin = new Usuario();
			admin.setRol(rolAdmin);
			admin.setCorreo(correoLimpio);
			admin.setContraseña(passwordEncoder.encode(adminPassword));
			admin.setNombre("Admin");
			admin.setApellido("Principal");
			admin.setDni("00000000");
			admin.setEstado(true);

			usuarioRepository.save(admin);
		}
	}

	private void crearRolSiNoExiste(String nombre) {
		if (rolRepository.findByNombre(nombre).isEmpty()) {
			Rol rol = new Rol();
			rol.setNombre(nombre);
			rolRepository.save(rol);
		}
	}
}
