package com.citas.medicas.cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.cita.dto.CitaRequest;
import com.citas.medicas.cita.dto.CitaResponse;

@RestController
@RequestMapping("/api/admin/citas")
public class CitasAdminController {

	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

	private final CitasService service;

	public CitasAdminController(CitasService service) {
		this.service = service;
	}

	@GetMapping
	public List<CitaResponse> listar(@RequestParam(required = false) String q,
									 @RequestParam(required = false) String estado,
									 @RequestParam(required = false) LocalDate fecha,
									 @RequestParam(required = false) Long idMedico) {
		return service.filtrar(q, estado, fecha, idMedico).stream()
				.map(CitaMapper::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public CitaResponse obtener(@PathVariable Long id) {
		return CitaMapper.toResponse(service.obtener(id));
	}

	@GetMapping("/turnos")
	public List<TurnoCitaRespuesta> turnos(@RequestParam Long idHorario,
										   @RequestParam LocalDate fecha) {
		return service.obtenerTurnosDisponibles(idHorario, fecha).stream()
				.map(turno -> new TurnoCitaRespuesta(
						turno.horaInicio().format(HORA),
						turno.horaFin().format(HORA)))
				.toList();
	}

	@PostMapping
	public ResponseEntity<CitaResponse> guardar(@RequestBody CitaRequest request) {
		Citas cita = service.registrar(
				request.idPaciente(),
				request.idMedico(),
				request.idHorario(),
				request.fecha(),
				parse(request.horaInicio()));
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(CitaMapper.toResponse(service.obtener(cita.getIdCita())));
	}

	@PatchMapping("/{id}/cancelar")
	public CitaResponse cancelar(@PathVariable Long id) {
		service.cancelar(id);
		return CitaMapper.toResponse(service.obtener(id));
	}

	private static LocalTime parse(String hora) {
		return hora == null || hora.isBlank() ? null : LocalTime.parse(hora.trim());
	}
}
