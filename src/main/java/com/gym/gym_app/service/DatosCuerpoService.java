package com.gym.gym_app.service;

import com.gym.gym_app.models.DatosCuerpo;
import com.gym.gym_app.repository.DatosCuerpoRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatosCuerpoService {

    private final DatosCuerpoRepository datosCuerpoRepository;
    private final UsuarioRepository usuarioRepository;

    public DatosCuerpoService(DatosCuerpoRepository datosCuerpoRepository,
                              UsuarioRepository usuarioRepository) {
        this.datosCuerpoRepository = datosCuerpoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DatosCuerpo guardar(Long usuarioId, DatosCuerpo datos) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con id " + usuarioId + " no encontrado"));

        datos.setUsuario(usuarioRepository.findById(usuarioId).get());
        return datosCuerpoRepository.save(datos);
    }

    public List<DatosCuerpo> getHistorial(Long usuarioId) {
        return datosCuerpoRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    public Optional<DatosCuerpo> getUltimo(Long usuarioId) {
        return datosCuerpoRepository.findTopByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }
}