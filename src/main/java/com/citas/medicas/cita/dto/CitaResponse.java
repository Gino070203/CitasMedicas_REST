package com.citas.medicas.cita.dto;

import java.time.LocalDate;

public record CitaResponse(
		Long idCita,
		Long idPaciente,
		String nombrePaciente,
		Long idMedico,
		String nombreMedico,
		String especialidadNombre,
		Long idHorario,
		LocalDate fecha,
		String diaSemana,
		String horaInicio,
		String horaFin,
		String estado,
		String diagnostico) {
}
