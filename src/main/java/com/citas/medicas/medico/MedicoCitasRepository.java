package com.citas.medicas.medico;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citas.medicas.cita.Citas;

public interface MedicoCitasRepository extends JpaRepository<Citas, Long> {

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.medico.idMedico = :idMedico
		  AND c.fecha = :fecha
		ORDER BY c.horaInicio
	""")
	List<Citas> agendaDelDia(@Param("idMedico") Long idMedico,
							 @Param("fecha") LocalDate fecha);

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.medico.idMedico = :idMedico
		  AND c.fecha >= :desde
		  AND (c.estado IS NULL
		       OR UPPER(c.estado) NOT IN ('CANCELADA', 'ATENDIDA', 'NO_ASISTIO'))
		ORDER BY c.fecha, c.horaInicio
	""")
	List<Citas> porAtender(@Param("idMedico") Long idMedico,
						   @Param("desde") LocalDate desde);

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.medico.idMedico = :idMedico
		  AND UPPER(c.estado) = 'ATENDIDA'
		ORDER BY c.fecha DESC, c.horaInicio DESC
	""")
	List<Citas> historial(@Param("idMedico") Long idMedico);

	@Query("""
		SELECT c FROM Citas c
		JOIN FETCH c.paciente p
		JOIN FETCH p.usuario
		JOIN FETCH c.medico m
		JOIN FETCH m.usuario
		JOIN FETCH m.especialidad
		JOIN FETCH c.horario
		WHERE c.idCita = :idCita
		  AND c.medico.idMedico = :idMedico
	""")
	Optional<Citas> buscarDelMedico(@Param("idCita") Long idCita,
									@Param("idMedico") Long idMedico);
}
