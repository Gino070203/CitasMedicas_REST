package com.citas.medicas.pago.dto;

import java.math.BigDecimal;

public record PagoRequest(
		Long idCita,
		BigDecimal monto,
		String metodoPago) {
}
