package com.gym.gym_app.repository;

import com.gym.gym_app.models.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByUsuarioIdOrderByFechaEntradaDesc(Long usuarioId);

    Optional<Asistencia> findTopByUsuarioIdAndFechaSalidaIsNullOrderByFechaEntradaDesc(Long usuarioId);

    List<Asistencia> findByFechaEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
}