package com.gym.gym_app.repository;

import com.gym.gym_app.models.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByUsuarioIdOrderByFechaEntradaDesc(Long usuarioId);

    Optional<Asistencia> findTopByUsuarioIdAndFechaSalidaIsNullOrderByFechaEntradaDesc(Long usuarioId);

    List<Asistencia> findByFechaEntradaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Asistencia> findByFechaSalidaIsNullAndFechaEntradaBefore(LocalDateTime fecha);

    // [usuarioId, fecha de su última entrada] por cada usuario que ha asistido alguna vez
    @Query("SELECT a.usuario.id, MAX(a.fechaEntrada) FROM Asistencia a GROUP BY a.usuario.id")
    List<Object[]> findUltimaEntradaPorUsuario();

}