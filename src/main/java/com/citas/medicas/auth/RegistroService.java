package com.citas.medicas.auth;

import java.util.Locale;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citas.medicas.rol.Rol;
import com.citas.medicas.rol.RolRepository;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

@Service
public class RegistroService {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final PasswordEncoder passwordEncoder;
	private final CorreoService correoService;

	public RegistroService(UsuarioRepository usuarioRepository,
						   RolRepository rolRepository,
						   PasswordEncoder passwordEncoder,
						   CorreoService correoService) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
		this.correoService = correoService;
	}

	@Transactional
	public Usuario registrar(String nombre, String apellido, String dni,
							 String correo, String password, String telefono,
							 String direccion) {
		if (nombre == null || nombre.isBlank()
				|| apellido == null || apellido.isBlank()) {
			throw new IllegalArgumentException("Nombre y apellido son obligatorios.");
		}
		if (correo == null || !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new IllegalArgumentException("Correo inválido.");
		}
		if (dni == null || !dni.matches("\\d{8}")) {
			throw new IllegalArgumentException("El DNI debe tener 8 dígitos.");
		}
		if (password == null || password.length() < 8) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
		}

		String correoLimpio = correo.trim().toLowerCase(Locale.ROOT);

		if (usuarioRepository.findByCorreo(correoLimpio).isPresent()) {
			throw new IllegalArgumentException("El correo electrónico ya está registrado.");
		}

		Rol rolPaciente = rolRepository.findByNombre("PACIENTE")
				.orElseGet(() -> {
					Rol nuevoRol = new Rol();
					nuevoRol.setNombre("PACIENTE");
					return rolRepository.save(nuevoRol);
				});

		Usuario usuario = new Usuario();
		usuario.setNombre(nombre.trim());
		usuario.setApellido(apellido.trim());
		usuario.setDni(dni);
		usuario.setCorreo(correoLimpio);
		usuario.setTelefono(telefono);
		usuario.setDireccion(direccion);
		usuario.setContraseña(passwordEncoder.encode(password));
		usuario.setRol(rolPaciente);
		usuario.setEstado(false);

		String codigo = String.format("%06d", new Random().nextInt(999999));
		usuario.setCodigoVerificacion(codigo);

		Usuario guardado = usuarioRepository.save(usuario);
		correoService.enviarCodigoVerificacion(usuario.getCorreo(), codigo);
		return guardado;
	}
}
