package com.gym.gym_app.repository;
import com.gym.gym_app.models.DatosCuerpo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatosCuerpoRepository extends JpaRepository<DatosCuerpo, Long> {

    List<DatosCuerpo> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);

    DatosCuerpo findTopByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
}
