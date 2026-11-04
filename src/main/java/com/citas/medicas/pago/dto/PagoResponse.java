package com.citas.medicas.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(
		Long idPago,
		Long idCita,
		String nombrePaciente,
		String nombreMedico,
		BigDecimal monto,
		LocalDate fechaPago,
		String metodoPago,
		String estado) {
}
