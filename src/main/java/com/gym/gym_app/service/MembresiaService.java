package com.gym.gym_app.service;

import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public MembresiaService(MembresiaRepository membresiaRepository,
                            UsuarioRepository usuarioRepository) {
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Membresia crearMembresia(Long usuarioId, Membresia membresia) {
        // Verificar que el usuario existe
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado"));

        // Desactivar membresía anterior si existe
        membresiaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                .ifPresent(m -> {
                    m.setActiva(false);
                    membresiaRepository.save(m);
                });

        // Configurar la nueva membresía
        membresia.setUsuario(usuarioRepository.findById(usuarioId).get());
        membresia.setFechaPago(LocalDate.now());
        membresia.setActiva(true);

        // Calcular vencimiento según tipo
        LocalDate vencimiento = switch (membresia.getTipo()) {
            case MENSUAL -> LocalDate.now().plusMonths(1);
            case TRIMESTRAL -> LocalDate.now().plusMonths(3);
            case SEMESTRAL -> LocalDate.now().plusMonths(6);
            case ANUAL -> LocalDate.now().plusYears(1);
        };
        membresia.setFechaVencimiento(vencimiento);

        return membresiaRepository.save(membresia);
    }

    public List<Membresia> getHistorialByUsuario(Long usuarioId) {
        return membresiaRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Membresia> getMembresiaActiva(Long usuarioId) {
        return membresiaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                .filter(m -> !m.getFechaVencimiento().isBefore(LocalDate.now()));
    }

    public void deleteMembresia(Long id) {
        membresiaRepository.deleteById(id);
    }

    public int verificarYMarcarVencidas() {
        List<Membresia> vencidas = membresiaRepository
                .findByActivaTrueAndFechaVencimientoBefore(java.time.LocalDate.now());
        vencidas.forEach(m -> m.setActiva(false));
        membresiaRepository.saveAll(vencidas);
        return vencidas.size();
    }
}