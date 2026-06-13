package com.gym.gym_app.service;

import com.gym.gym_app.models.Asistencia;
import com.gym.gym_app.repository.AsistenciaRepository;
import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MembresiaRepository membresiaRepository;
    private static final Logger log = LoggerFactory.getLogger(AsistenciaService.class);

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                             UsuarioRepository usuarioRepository,
                             MembresiaRepository membresiaRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.membresiaRepository = membresiaRepository;
    }

    public Asistencia registrarEntrada(Long usuarioId) {
        // Verificar que el usuario existe
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuario con id " + usuarioId + " no encontrado"));

        // Verificar membresía activa
        membresiaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario no tiene una membresía activa"));

        // Verificar que no haya una entrada sin salida
        asistenciaRepository
                .findTopByUsuarioIdAndFechaSalidaIsNullOrderByFechaEntradaDesc(usuarioId)
                .ifPresent(a -> { throw new IllegalStateException(
                        "El usuario ya tiene una entrada activa sin registrar salida"); });

        Asistencia asistencia = new Asistencia();
        asistencia.setUsuario(usuarioRepository.findById(usuarioId).get());
        return asistenciaRepository.save(asistencia);
    }

    public Asistencia registrarSalida(Long usuarioId) {
        Asistencia asistencia = asistenciaRepository
                .findTopByUsuarioIdAndFechaSalidaIsNullOrderByFechaEntradaDesc(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No hay entrada activa para este usuario"));

        asistencia.setFechaSalida(LocalDateTime.now());
        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> getHistorial(Long usuarioId) {
        return asistenciaRepository.findByUsuarioIdOrderByFechaEntradaDesc(usuarioId);
    }

    public List<Asistencia> getAsistenciasHoy() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);
        return asistenciaRepository.findByFechaEntradaBetween(inicioDia, finDia);
    }

    public void registrarSalidasAutomaticas() {
        LocalDateTime haceDosHoras = LocalDateTime.now().minusHours(2);
        List<Asistencia> sinSalida = asistenciaRepository
                .findByFechaSalidaIsNullAndFechaEntradaBefore(haceDosHoras);

        if (sinSalida.isEmpty()) return;

        sinSalida.forEach(a -> a.setFechaSalida(LocalDateTime.now()));
        asistenciaRepository.saveAll(sinSalida);

        log.info("Se registraron {} salidas automáticas.", sinSalida.size());
    }
}