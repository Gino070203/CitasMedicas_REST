package com.citas.medicas.usuario;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.auth.dto.UsuarioMeResponse;
import com.citas.medicas.security.UsuarioAutenticado;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	private final UsuarioRepository usuarioRepository;

	public UsuarioController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@GetMapping("/me")
	public UsuarioMeResponse perfilActual(Authentication authentication) {
		if (!(authentication.getPrincipal() instanceof UsuarioAutenticado principal)) {
			throw new IllegalStateException("No autenticado");
		}

		Usuario usuario = usuarioRepository.findById(principal.idUsuario())
				.orElseThrow(() -> new IllegalStateException("No se encontró la cuenta."));

		return new UsuarioMeResponse(
				principal.idUsuario(),
				principal.correo(),
				usuario.getNombre(),
				usuario.getApellido(),
				principal.rol());
	}
}
