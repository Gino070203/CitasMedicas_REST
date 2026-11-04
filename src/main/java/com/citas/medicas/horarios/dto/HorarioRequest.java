package com.citas.medicas.horarios.dto;

public record HorarioRequest(
		Long idMedico,
		String diaSemana,
		String horaInicio,
		String horaFin) {
}
