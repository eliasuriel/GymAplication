package com.gym.gym_app.repository;
import com.gym.gym_app.models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    @Query("SELECT COALESCE(MAX(u.id), 0) FROM Usuario u")
    Long findMaxId();
}