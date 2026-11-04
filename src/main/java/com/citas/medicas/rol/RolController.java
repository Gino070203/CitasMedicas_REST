package com.citas.medicas.rol;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rol")
public class RolController {

	private final RolService rolService;

	public RolController(RolService rolService) {
		this.rolService = rolService;
	}

	@PostMapping
	public void agregarRol(@RequestParam String nombreRol) {
		rolService.agregarRol(nombreRol);
	}
}
