package com.citas.medicas.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citas.medicas.dashboard.dto.DashboardResponse;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

	private final DashboardService service;

	public AdminDashboardController(DashboardService service) {
		this.service = service;
	}

	@GetMapping
	public DashboardResponse dashboard() {
		return new DashboardResponse(
				service.totalUsuarios(),
				service.totalMedicos(),
				service.totalPacientes(),
				service.totalEspecialidades(),
				service.totalCitas(),
				service.citasHoy(),
				service.totalPagos());
	}
}
