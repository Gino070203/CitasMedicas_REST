package com.citas.medicas.cita.dto;

import java.time.LocalDate;

public record CitaRequest(
		Long idPaciente,
		Long idMedico,
		Long idHorario,
		LocalDate fecha,
		String horaInicio) {
}
