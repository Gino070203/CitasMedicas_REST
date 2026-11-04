package com.citas.medicas.medico;

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

import com.citas.medicas.medico.dto.MedicoRequest;
import com.citas.medicas.medico.dto.MedicoResponse;
import com.citas.medicas.medico.dto.MedicoUpdateRequest;

@RestController
@RequestMapping("/api/admin/medicos")
public class MedicoAdminController {

	private final MedicoService service;

	public MedicoAdminController(MedicoService service) {
		this.service = service;
	}

	@GetMapping
	public List<MedicoResponse> listar(@RequestParam(required = false) String q,
									   @RequestParam(required = false) Long idEspecialidad,
									   @RequestParam(required = false) Boolean estado) {
		return service.filtrar(q, idEspecialidad, estado).stream()
				.map(MedicoAdminController::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public MedicoResponse obtener(@PathVariable Long id) {
		return toResponse(service.obtener(id));
	}

	@PostMapping
	public ResponseEntity<MedicoResponse> registrar(@RequestBody MedicoRequest request) {
		Medico medico = service.registrar(request.idUsuario(), request.idEspecialidad(), request.cmp());
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(medico));
	}

	@PutMapping("/{id}")
	public MedicoResponse actualizar(@PathVariable Long id,
									 @RequestBody MedicoUpdateRequest request) {
		Medico medico = service.actualizar(id, request.idEspecialidad(), request.cmp());
		return toResponse(medico);
	}

	@PatchMapping("/{id}/estado")
	public MedicoResponse cambiarEstado(@PathVariable Long id) {
		service.cambiarEstado(id);
		return toResponse(service.obtener(id));
	}

	private static MedicoResponse toResponse(Medico m) {
		var usuario = m.getUsuario();
		var especialidad = m.getEspecialidad();
		return new MedicoResponse(
				m.getIdMedico(),
				usuario != null ? usuario.getIdUsuario() : null,
				usuario != null ? usuario.getNombre() : null,
				usuario != null ? usuario.getApellido() : null,
				m.getCmp(),
				especialidad != null ? especialidad.getIdEspecialidad() : null,
				especialidad != null ? especialidad.getNombre() : null,
				m.getEstado());
	}
}
