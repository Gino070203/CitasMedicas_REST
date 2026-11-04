package com.citas.medicas.paciente.dto;

public record ReservaRequest(
		Long idHorario,
		java.time.LocalDate fecha,
		String horaInicio) {
}
