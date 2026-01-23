package com.gym.gym_app.service;

import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.models.Membresia;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    public List<Membresia> getMembresiasByUsuario(Long usuarioId) {
        return membresiaRepository.findByUsuarioId(usuarioId);
    }

    public Membresia getUltimaMembresia(Long usuarioId) {
        return membresiaRepository.findTopByUsuarioIdOrderByFechaVencimientoDesc(usuarioId);
    }

    public Membresia saveMembresia(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void deleteMembresia(Long id) {
        membresiaRepository.deleteById(id);
    }
}
