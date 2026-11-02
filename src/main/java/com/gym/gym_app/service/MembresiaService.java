package com.gym.gym_app.service;

import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
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
        if (membresia.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de membresía es obligatorio");
        }
        if (membresia.getPrecio() == null || membresia.getPrecio().signum() <= 0) {
            throw new IllegalArgumentException("El precio es obligatorio y debe ser mayor a 0");
        }
        // Verificar que el usuario existe
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + usuarioId + " no encontrado"));

        // Fecha de inicio: la que manda el administrador o, si no manda ninguna, hoy
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = membresia.getFechaPago() != null ? membresia.getFechaPago() : hoy;
        if (inicio.isAfter(hoy)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser futura");
        }

        // Calcular vencimiento según tipo, a partir de la fecha de inicio
        LocalDate vencimiento = switch (membresia.getTipo()) {
            case MENSUAL -> inicio.plusMonths(1);
            case TRIMESTRAL -> inicio.plusMonths(3);
            case SEMESTRAL -> inicio.plusMonths(6);
            case ANUAL -> inicio.plusYears(1);
        };

        // Si ya venció (se está registrando un pago antiguo), queda como histórico
        // y NO desplaza a la membresía vigente de la clienta.
        boolean vigente = !vencimiento.isBefore(hoy);
        if (vigente) {
            // Desactivar membresía anterior si existe
            membresiaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                    .ifPresent(m -> {
                        m.setActiva(false);
                        membresiaRepository.save(m);
                    });
        }

        // Configurar la nueva membresía
        membresia.setId(null);
        membresia.setUsuario(usuarioRepository.findById(usuarioId).get());
        membresia.setFechaPago(inicio);
        membresia.setFechaVencimiento(vencimiento);
        membresia.setActiva(vigente);

        return membresiaRepository.save(membresia);
    }

    public List<Membresia> getHistorialByUsuario(Long usuarioId) {
        return membresiaRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Membresia> getMembresiaActiva(Long usuarioId) {
        return membresiaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                .filter(m -> !m.getFechaVencimiento().isBefore(LocalDate.now()));
    }

    /**
     * Elimina una membresía (por ejemplo, una creada por error).
     * Si era la activa y la clienta tiene otra anterior que sigue vigente, esa vuelve a quedar activa.
     */
    public void deleteMembresia(Long id) {
        Membresia membresia = membresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía con id " + id + " no encontrada"));

        Long usuarioId = membresia.getUsuario().getId();
        boolean eraActiva = membresia.isActiva();

        membresiaRepository.delete(membresia);

        if (eraActiva) {
            LocalDate hoy = LocalDate.now();
            membresiaRepository.findByUsuarioId(usuarioId).stream()
                    .filter(m -> !m.getFechaVencimiento().isBefore(hoy))
                    .max(Comparator.comparing(Membresia::getFechaVencimiento))
                    .ifPresent(m -> {
                        m.setActiva(true);
                        membresiaRepository.save(m);
                    });
        }
    }

    public int verificarYMarcarVencidas() {
        List<Membresia> vencidas = membresiaRepository
                .findByActivaTrueAndFechaVencimientoBefore(java.time.LocalDate.now());
        vencidas.forEach(m -> m.setActiva(false));
        membresiaRepository.saveAll(vencidas);
        return vencidas.size();
    }
}