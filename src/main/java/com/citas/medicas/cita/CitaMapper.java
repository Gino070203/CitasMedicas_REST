package com.citas.medicas.cita;

import java.time.format.DateTimeFormatter;

import com.citas.medicas.cita.dto.CitaResponse;

public final class CitaMapper {

	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

	private CitaMapper() {
	}

	public static CitaResponse toResponse(Citas c) {
		String nombrePaciente = null;
		if (c.getPaciente() != null && c.getPaciente().getUsuario() != null) {
			nombrePaciente = nombreCompleto(c.getPaciente().getUsuario().getNombre(),
					c.getPaciente().getUsuario().getApellido());
		}
		String nombreMedico = null;
		if (c.getMedico() != null && c.getMedico().getUsuario() != null) {
			nombreMedico = nombreCompleto(c.getMedico().getUsuario().getNombre(),
					c.getMedico().getUsuario().getApellido());
		}
		String especialidadNombre = null;
		if (c.getMedico() != null && c.getMedico().getEspecialidad() != null) {
			especialidadNombre = c.getMedico().getEspecialidad().getNombre();
		}
		String diaSemana = c.getHorario() != null ? c.getHorario().getDiaSemana() : null;

		return new CitaResponse(
				c.getIdCita(),
				c.getPaciente() != null ? c.getPaciente().getIdPaciente() : null,
				nombrePaciente,
				c.getMedico() != null ? c.getMedico().getIdMedico() : null,
				nombreMedico,
				especialidadNombre,
				c.getHorario() != null ? c.getHorario().getIdHorario() : null,
				c.getFecha(),
				diaSemana,
				c.getHoraInicio() != null ? c.getHoraInicio().format(HORA) : null,
				c.getHoraFin() != null ? c.getHoraFin().format(HORA) : null,
				c.getEstado(),
				c.getDiagnostico());
	}

	private static String nombreCompleto(String nombre, String apellido) {
		String n = nombre == null ? "" : nombre.trim();
		String a = apellido == null ? "" : apellido.trim();
		return (n + " " + a).trim();
	}
}
