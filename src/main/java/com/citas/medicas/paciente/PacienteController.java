package com.citas.medicas.paciente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.cita.CitaMapper;
import com.citas.medicas.cita.Citas;
import com.citas.medicas.cita.CitasService;
import com.citas.medicas.cita.TurnoCitaRespuesta;
import com.citas.medicas.cita.dto.CitaResponse;
import com.citas.medicas.especialidad.Especialidad;
import com.citas.medicas.especialidad.EspecialidadService;
import com.citas.medicas.especialidad.dto.EspecialidadResponse;
import com.citas.medicas.horarios.Horarios;
import com.citas.medicas.horarios.dto.HorarioResponse;
import com.citas.medicas.paciente.dto.ReservaRequest;
import com.citas.medicas.security.UsuarioAutenticado;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/paciente")
public class PacienteController {

	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

	private final PacienteService pacienteService;
	private final EspecialidadService especialidadService;
	private final CitasService citasService;
	private final PerfilPacienteService perfilPacienteService;

	public PacienteController(PacienteService pacienteService,
							  EspecialidadService especialidadService,
							  CitasService citasService,
							  PerfilPacienteService perfilPacienteService) {
		this.pacienteService = pacienteService;
		this.especialidadService = especialidadService;
		this.citasService = citasService;
		this.perfilPacienteService = perfilPacienteService;
	}

	@GetMapping("/perfil")
	public PerfilPacienteVista mostrarPerfil(Authentication authentication) {
		return perfilPacienteService.obtenerPerfil(idUsuarioActual(authentication));
	}

	@PutMapping("/perfil")
	public PerfilPacienteVista actualizarPerfil(Authentication authentication,
												@Valid @RequestBody PerfilPacienteForm formulario) {
		Long idUsuario = idUsuarioActual(authentication);
		perfilPacienteService.actualizarPerfil(idUsuario, formulario);
		return perfilPacienteService.obtenerPerfil(idUsuario);
	}

	@GetMapping("/especialidades")
	public List<EspecialidadResponse> listarEspecialidades() {
		return especialidadService.activas().stream()
				.map(PacienteController::toEspecialidadResponse)
				.toList();
	}

	@GetMapping("/horarios")
	public List<HorarioResponse> listarHorarios(@RequestParam Long idEspecialidad) {
		List<Horarios> horarios = pacienteService.obtenerHorariosPorEspecialidad(idEspecialidad);
		LocalDate desde = LocalDate.now();
		LocalDate hasta = desde.plusDays(90);
		Map<Long, String> fechasDisponibles =
				citasService.obtenerFechasDisponiblesPorHorario(horarios, desde, hasta);

		return horarios.stream()
				.map(h -> toHorarioResponse(h, fechasDisponibles.get(h.getIdHorario())))
				.toList();
	}

	@GetMapping("/turnos")
	public List<TurnoCitaRespuesta> obtenerTurnos(@RequestParam Long idHorario,
												  @RequestParam java.time.LocalDate fecha) {
		return citasService.obtenerTurnosDisponibles(idHorario, fecha).stream()
				.map(turno -> new TurnoCitaRespuesta(
						turno.horaInicio().format(HORA),
						turno.horaFin().format(HORA)))
				.toList();
	}

	@PostMapping("/reservar")
	public CitaResponse reservar(Authentication authentication,
								 @RequestBody ReservaRequest request) {
		Paciente paciente = pacienteService.obtenerPacientePorUsuarioId(
				idUsuarioActual(authentication));

		if (paciente == null) {
			throw new IllegalStateException("Perfil de paciente no encontrado.");
		}

		Citas cita = pacienteService.reservarCita(
				paciente.getIdPaciente(), request.idHorario(), request.fecha(),
				parse(request.horaInicio()));
		return CitaMapper.toResponse(citasService.obtener(cita.getIdCita()));
	}

	@GetMapping("/citas")
	public List<CitaResponse> listarHistorial(Authentication authentication) {
		Paciente paciente = pacienteService.obtenerPacientePorUsuarioId(
				idUsuarioActual(authentication));
		if (paciente == null) {
			return List.of();
		}
		return pacienteService.obtenerHistorialCitas(paciente.getIdPaciente()).stream()
				.map(CitaMapper::toResponse)
				.toList();
	}

	private Long idUsuarioActual(Authentication authentication) {
		if (authentication.getPrincipal() instanceof UsuarioAutenticado principal
				&& principal.idUsuario() != null) {
			return principal.idUsuario();
		}
		throw new IllegalStateException(
				"No se pudo identificar tu cuenta. Vuelve a iniciar sesión.");
	}

	private static EspecialidadResponse toEspecialidadResponse(Especialidad e) {
		return new EspecialidadResponse(e.getIdEspecialidad(), e.getNombre(), e.getDescripcion(), e.getEstado());
	}

	private static HorarioResponse toHorarioResponse(Horarios h, String fechasDisponibles) {
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
				0,
				fechasDisponibles);
	}

	private static LocalTime parse(String hora) {
		return hora == null || hora.isBlank() ? null : LocalTime.parse(hora.trim());
	}
}
