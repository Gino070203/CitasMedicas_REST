package com.citas.medicas.dashboard.dto;

public record DashboardResponse(
		long totalUsuarios,
		long totalMedicos,
		long totalPacientes,
		long totalEspecialidades,
		long totalCitas,
		long citasHoy,
		long totalPagos) {
}
