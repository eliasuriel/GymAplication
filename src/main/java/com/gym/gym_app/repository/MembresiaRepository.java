package com.gym.gym_app.repository;

import com.gym.gym_app.models.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByUsuarioId(Long usuarioId);

    Optional<Membresia> findByUsuarioIdAndActivaTrue(Long usuarioId);

    List<Membresia> findByActivaTrueAndFechaVencimientoBefore(java.time.LocalDate fecha);
}