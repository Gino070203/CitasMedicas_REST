package com.citas.medicas.cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CitasRepository extends JpaRepository<Citas, Long> {

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		ORDER BY c.fecha DESC, c.horaInicio
	""")
	List<Citas> listarCitas();

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.idCita = :id
	""")
	java.util.Optional<Citas> buscarPorIdConDatos(@Param("id") Long id);

	@Query("""
		SELECT DISTINCT c FROM Citas c
		LEFT JOIN FETCH c.paciente p
		LEFT JOIN FETCH p.usuario pu
		LEFT JOIN FETCH c.medico m
		LEFT JOIN FETCH m.usuario mu
		LEFT JOIN FETCH m.especialidad e
		LEFT JOIN FETCH c.horario h
		WHERE (:texto IS NULL
		       OR LOWER(COALESCE(pu.nombre, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR LOWER(COALESCE(pu.apellido, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR LOWER(COALESCE(pu.dni, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR LOWER(COALESCE(mu.nombre, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR LOWER(COALESCE(mu.apellido, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR LOWER(COALESCE(e.nombre, '')) LIKE CONCAT('%', LOWER(:texto), '%')
		       OR (:idCita IS NOT NULL AND c.idCita = :idCita))
		  AND (:estado IS NULL OR UPPER(c.estado) = UPPER(:estado))
		  AND (:fecha IS NULL OR c.fecha = :fecha)
		  AND (:idMedico IS NULL OR m.idMedico = :idMedico)
		ORDER BY c.fecha DESC, c.horaInicio
	""")
	List<Citas> filtrarCitas(
			@Param("texto") String texto,
			@Param("idCita") Long idCita,
			@Param("estado") String estado,
			@Param("fecha") LocalDate fecha,
			@Param("idMedico") Long idMedico);

	@Query("""
		SELECT c FROM Citas c
		WHERE c.medico.idMedico = :idMedico
		  AND c.fecha = :fecha
		ORDER BY c.horaInicio
	""")
	List<Citas> buscarPorMedicoYFecha(
			@Param("idMedico") Long idMedico,
			@Param("fecha") LocalDate fecha);

	@Query("""
		SELECT c FROM Citas c
		WHERE c.paciente.idPaciente = :idPaciente
		ORDER BY c.fecha DESC
	""")
	List<Citas> buscarPorPaciente(
			@Param("idPaciente") Long idPaciente);

	@Query("""
		SELECT COUNT(c) FROM Citas c
		WHERE c.medico.idMedico = :idMedico
		  AND c.fecha = :fecha
		  AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
		  AND c.horaInicio < :fin
		  AND c.horaFin > :inicio
		  AND (:excluirId IS NULL OR c.idCita <> :excluirId)
	""")
	long contarConflictos(
			@Param("idMedico") Long idMedico,
			@Param("fecha") LocalDate fecha,
			@Param("inicio") LocalTime inicio,
			@Param("fin") LocalTime fin,
			@Param("excluirId") Long excluirId);

	@Query("""
		SELECT c FROM Citas c
		WHERE c.medico.idMedico IN :idsMedico
		  AND c.fecha BETWEEN :desde AND :hasta
		  AND (c.estado IS NULL OR UPPER(c.estado) <> 'CANCELADA')
		ORDER BY c.fecha, c.horaInicio
	""")
	List<Citas> buscarNoCanceladasPorMedicosEnRango(
			@Param("idsMedico") List<Long> idsMedico,
			@Param("desde") LocalDate desde,
			@Param("hasta") LocalDate hasta);

	@Query("""
		SELECT COUNT(c) FROM Citas c
		WHERE c.fecha = :fecha
	""")
	long contarCitasDelDia(
			@Param("fecha") LocalDate fecha);

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.paciente.idPaciente = :idPaciente
		ORDER BY c.fecha DESC, c.horaInicio DESC
	""")
	List<Citas> findByPacienteId(@Param("idPaciente") Long idPaciente);

	@Query("SELECT c FROM Citas c WHERE c.paciente.idPaciente = :idPaciente AND c.estado = :estado")
	List<Citas> findByPacienteIdAndEstado(@Param("idPaciente") Long idPaciente, @Param("estado") String estado);

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE UPPER(c.estado) = 'PENDIENTE_PAGO'
	""")
	List<Citas> listarPendientesPago();

}
