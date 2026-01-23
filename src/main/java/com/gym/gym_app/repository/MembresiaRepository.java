package com.gym.gym_app.repository;
import com.gym.gym_app.models.Membresia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByUsuarioId(Long usuarioId);

    Membresia findTopByUsuarioIdOrderByFechaVencimientoDesc(Long usuarioId);
}
