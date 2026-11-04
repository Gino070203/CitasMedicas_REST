package com.citas.medicas.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
		@NotBlank(message = "El nombre es obligatorio.") @Size(max = 60) String nombre,
		@NotBlank(message = "El apellido es obligatorio.") @Size(max = 60) String apellido,
		@NotBlank(message = "El DNI es obligatorio.") @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos.") String dni,
		@NotBlank(message = "El correo es obligatorio.") @Email(message = "Correo inválido.") String correo,
		@NotBlank(message = "La contraseña es obligatoria.") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.") String password,
		@Size(max = 20) String telefono,
		@Size(max = 100) String direccion) {
}
