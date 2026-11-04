package com.citas.medicas.paciente;

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

import com.citas.medicas.paciente.dto.PacienteActualizacionRequest;
import com.citas.medicas.paciente.dto.PacienteRegistroRequest;
import com.citas.medicas.paciente.dto.PacienteResponse;

@RestController
@RequestMapping("/api/admin/pacientes")
public class PacienteAdminController {

	private final PacienteService service;

	public PacienteAdminController(PacienteService service) {
		this.service = service;
	}

	@GetMapping
	public List<PacienteResponse> listar(@RequestParam(required = false) String q,
										 @RequestParam(required = false) Boolean estado,
										 @RequestParam(required = false) Boolean conSis) {
		return service.filtrar(q, estado, conSis).stream()
				.map(PacienteAdminController::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public PacienteResponse obtener(@PathVariable Long id) {
		return toResponse(service.obtener(id));
	}

	@PostMapping
	public ResponseEntity<PacienteResponse> registrar(@RequestBody PacienteRegistroRequest request) {
		Paciente paciente = service.registrar(request.idUsuario(), request.numeroHistoria(),
				request.sis(), request.fechaNacimiento(), request.genero(), request.direccion());
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(paciente));
	}

	@PutMapping("/{id}")
	public PacienteResponse actualizar(@PathVariable Long id,
									   @RequestBody PacienteActualizacionRequest request) {
		Paciente paciente = service.actualizar(id, request.numeroHistoria(), request.sis(),
				request.fechaNacimiento(), request.genero(), request.direccion(),
				request.numeroHistoriaVerificado(), request.sisVerificado());
		return toResponse(paciente);
	}

	@PatchMapping("/{id}/estado")
	public PacienteResponse cambiarEstado(@PathVariable Long id) {
		service.cambiarEstado(id);
		return toResponse(service.obtener(id));
	}

	private static PacienteResponse toResponse(Paciente p) {
		var usuario = p.getUsuario();
		return new PacienteResponse(
				p.getIdPaciente(),
				usuario != null ? usuario.getIdUsuario() : null,
				usuario != null ? usuario.getNombre() : null,
				usuario != null ? usuario.getApellido() : null,
				usuario != null ? usuario.getDni() : null,
				usuario != null ? usuario.getCorreo() : null,
				p.getNumeroHistoria(),
				p.getSis(),
				p.getNumeroHistoriaVerificado(),
				p.getSisVerificado(),
				p.getFechaNacimiento(),
				p.getGenero(),
				p.getDireccion(),
				p.getEstado());
	}
}
