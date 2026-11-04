package com.citas.medicas.horarios;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

import com.citas.medicas.horarios.dto.HorarioRequest;
import com.citas.medicas.horarios.dto.HorarioResponse;

@RestController
@RequestMapping("/api/admin/horarios")
public class HorariosAdminController {

	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

	private final HorariosService service;

	public HorariosAdminController(HorariosService service) {
		this.service = service;
	}

	@GetMapping
	public List<HorarioResponse> listar(@RequestParam(required = false) String q,
										@RequestParam(required = false) String diaSemana,
										@RequestParam(required = false) Boolean estado) {
		return service.filtrar(q, diaSemana, estado).stream()
				.map(this::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public HorarioResponse obtener(@PathVariable Long id) {
		return toResponse(service.obtener(id));
	}

	@PostMapping
	public ResponseEntity<HorarioResponse> crear(@RequestBody HorarioRequest request) {
		Horarios horario = service.guardar(null, request.idMedico(), request.diaSemana(),
				parse(request.horaInicio()), parse(request.horaFin()));
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(toResponse(service.obtener(horario.getIdHorario())));
	}

	@PutMapping("/{id}")
	public HorarioResponse actualizar(@PathVariable Long id,
									  @RequestBody HorarioRequest request) {
		Horarios horario = service.guardar(id, request.idMedico(), request.diaSemana(),
				parse(request.horaInicio()), parse(request.horaFin()));
		return toResponse(service.obtener(horario.getIdHorario()));
	}

	@PatchMapping("/{id}/estado")
	public HorarioResponse cambiarEstado(@PathVariable Long id) {
		service.cambiarEstado(id);
		return toResponse(service.obtener(id));
	}

	private HorarioResponse toResponse(Horarios h) {
		String nombreMedico = null;
		String especialidadNombre = null;
		if (h.getMedico() != null) {
			if (h.getMedico().getUsuario() != null) {
				nombreMedico = h.getMedico().getUsuario().getNombre() + " "
						+ h.getMedico().getUsuario().getApellido();
			}
			if (h.getMedico().getEspecialidad() != null) {
				especialidadNombre = h.getMedico().getEspecialidad().getNombre();
			}
		}
		return new HorarioResponse(
				h.getIdHorario(),
				h.getMedico() != null ? h.getMedico().getIdMedico() : null,
				nombreMedico,
				especialidadNombre,
				h.getDiaSemana(),
				h.getHoraInicio() != null ? h.getHoraInicio().format(HORA) : null,
				h.getHoraFin() != null ? h.getHoraFin().format(HORA) : null,
				h.getEstado(),
				service.contarTurnosPosibles(h),
				null);
	}

	private static LocalTime parse(String hora) {
		return hora == null || hora.isBlank() ? null : LocalTime.parse(hora.trim());
	}
}
