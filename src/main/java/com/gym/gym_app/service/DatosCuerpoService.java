package com.gym.gym_app.service;

import com.gym.gym_app.repository.DatosCuerpoRepository;
import com.gym.gym_app.models.DatosCuerpo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatosCuerpoService {

    private final DatosCuerpoRepository datosCuerpoRepository;

    public DatosCuerpoService(DatosCuerpoRepository datosCuerpoRepository) {
        this.datosCuerpoRepository = datosCuerpoRepository;
    }

    public List<DatosCuerpo> getHistorial(Long usuarioId) {
        return datosCuerpoRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    public DatosCuerpo getUltimoDato(Long usuarioId) {
        return datosCuerpoRepository.findTopByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    public DatosCuerpo guardarDatos(DatosCuerpo datos) {
        return datosCuerpoRepository.save(datos);
    }

    public void eliminarDatos(Long id) {
        datosCuerpoRepository.deleteById(id);
    }
}
