package com.gym.gym_app.repository;

import com.gym.gym_app.models.DatosCuerpo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

public interface DatosCuerpoRepository extends JpaRepository<DatosCuerpo, Long> {

    List<DatosCuerpo> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
    List<DatosCuerpo> findByFechaRegistroBetween(LocalDate inicio, LocalDate fin);
    Optional<DatosCuerpo> findTopByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
}