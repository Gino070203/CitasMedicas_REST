package com.citas.medicas.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record VerificarRequest(
		@NotBlank(message = "El correo es obligatorio.") String correo,
		@NotBlank(message = "El código es obligatorio.") String codigo) {
}
