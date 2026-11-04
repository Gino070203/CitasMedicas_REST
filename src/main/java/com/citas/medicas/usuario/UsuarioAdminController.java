package com.citas.medicas.usuario;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.usuario.dto.UsuarioRequest;
import com.citas.medicas.usuario.dto.UsuarioResponse;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

	private final UsuarioAdminService service;

	public UsuarioAdminController(UsuarioAdminService service) {
		this.service = service;
	}

	@GetMapping
	public List<UsuarioResponse> listar(@RequestParam(required = false) String q,
										@RequestParam(required = false) String rol,
										@RequestParam(required = false) Boolean estado) {
		return service.filtrar(q, rol, estado).stream()
				.map(UsuarioAdminController::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public UsuarioResponse obtener(@PathVariable Long id) {
		return toResponse(service.obtener(id));
	}

	@PostMapping
	public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) {
		Usuario usuario = service.guardar(null, request.nombre(), request.apellido(),
				request.dni(), request.correo(), request.telefono(), request.direccion(),
				request.password(), request.rol());
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(usuario));
	}

	@PutMapping("/{id}")
	public UsuarioResponse actualizar(@PathVariable Long id,
									  @RequestBody UsuarioRequest request) {
		Usuario usuario = service.guardar(id, request.nombre(), request.apellido(),
				request.dni(), request.correo(), request.telefono(), request.direccion(),
				request.password(), request.rol());
		return toResponse(usuario);
	}

	@PatchMapping("/{id}/estado")
	public UsuarioResponse cambiarEstado(@PathVariable Long id) {
		service.cambiarEstado(id);
		return toResponse(service.obtener(id));
	}

	private static UsuarioResponse toResponse(Usuario u) {
		return new UsuarioResponse(
				u.getIdUsuario(),
				u.getNombre(),
				u.getApellido(),
				u.getDni(),
				u.getCorreo(),
				u.getTelefono(),
				u.getDireccion(),
				u.getEstado(),
				u.getRol() != null ? u.getRol().getNombre() : null);
	}
}
