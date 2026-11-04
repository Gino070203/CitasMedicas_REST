package com.citas.medicas.especialidad;

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

import com.citas.medicas.especialidad.dto.EspecialidadRequest;
import com.citas.medicas.especialidad.dto.EspecialidadResponse;

@RestController
@RequestMapping("/api/admin/especialidades")
public class EspecialidadAdminController {

	private final EspecialidadService service;

	public EspecialidadAdminController(EspecialidadService service) {
		this.service = service;
	}

	@GetMapping
	public List<EspecialidadResponse> listar(@RequestParam(required = false) String q,
											 @RequestParam(required = false) Boolean estado) {
		return service.filtrar(q, estado).stream()
				.map(EspecialidadAdminController::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public EspecialidadResponse obtener(@PathVariable Long id) {
		return toResponse(service.obtener(id));
	}

	@PostMapping
	public ResponseEntity<EspecialidadResponse> crear(@RequestBody EspecialidadRequest request) {
		Especialidad especialidad = service.guardar(null, request.nombre(), request.descripcion());
		return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(especialidad));
	}

	@PutMapping("/{id}")
	public EspecialidadResponse actualizar(@PathVariable Long id,
										   @RequestBody EspecialidadRequest request) {
		Especialidad especialidad = service.guardar(id, request.nombre(), request.descripcion());
		return toResponse(especialidad);
	}

	@PatchMapping("/{id}/estado")
	public EspecialidadResponse cambiarEstado(@PathVariable Long id) {
		service.cambiarEstado(id);
		return toResponse(service.obtener(id));
	}

	private static EspecialidadResponse toResponse(Especialidad e) {
		return new EspecialidadResponse(
				e.getIdEspecialidad(),
				e.getNombre(),
				e.getDescripcion(),
				e.getEstado());
	}
}
