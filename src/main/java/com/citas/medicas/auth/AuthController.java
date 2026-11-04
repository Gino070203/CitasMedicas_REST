package com.citas.medicas.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.auth.dto.AuthResponse;
import com.citas.medicas.auth.dto.LoginRequest;
import com.citas.medicas.auth.dto.RegistroRequest;
import com.citas.medicas.auth.dto.VerificarRequest;
import com.citas.medicas.security.jwt.JwtTokenProvider;
import com.citas.medicas.usuario.Usuario;
import com.citas.medicas.usuario.UsuarioRepository;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;
	private final UsuarioRepository usuarioRepository;
	private final RegistroService registroService;
	private final VerificacionRegistroService verificacionRegistroService;

	public AuthController(AuthenticationManager authenticationManager,
						  JwtTokenProvider tokenProvider,
						  UsuarioRepository usuarioRepository,
						  RegistroService registroService,
						  VerificacionRegistroService verificacionRegistroService) {
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
		this.usuarioRepository = usuarioRepository;
		this.registroService = registroService;
		this.verificacionRegistroService = verificacionRegistroService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.correo(), request.password()));

			String correo = authentication.getName();
			String rol = authentication.getAuthorities().stream()
					.findFirst()
					.map(GrantedAuthority::getAuthority)
					.map(authority -> authority.replaceFirst("^ROLE_", ""))
					.orElse("USUARIO");

			Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
			Long idUsuario = usuario != null ? usuario.getIdUsuario() : null;
			String nombre = usuario != null ? usuario.getNombre() : null;
			String apellido = usuario != null ? usuario.getApellido() : null;

			String token = tokenProvider.generarToken(correo, idUsuario, rol);

			return ResponseEntity.ok(new AuthResponse(
					token, correo, rol, nombre, apellido, idUsuario,
					tokenProvider.expiracionSegundos()));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Credenciales incorrectas o cuenta inactiva."));
		}
	}

	@PostMapping("/registro")
	public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {
		Usuario usuario = registroService.registrar(
				request.nombre(), request.apellido(), request.dni(),
				request.correo(), request.password(),
				request.telefono(), request.direccion());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
				"mensaje", "Registro exitoso. Revisa tu correo para verificar tu cuenta.",
				"correo", usuario.getCorreo()));
	}

	@PostMapping("/verificar")
	public ResponseEntity<?> verificar(@Valid @RequestBody VerificarRequest request) {
		boolean verificado = verificacionRegistroService.verificarCodigo(
				request.correo(), request.codigo());

		if (!verificado) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Código de verificación incorrecto."));
		}

		return ResponseEntity.ok(Map.of("mensaje", "Cuenta verificada correctamente."));
	}
}
