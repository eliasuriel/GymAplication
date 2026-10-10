package com.gym.gym_app.service;

import com.gym.gym_app.models.Asistencia;
import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.models.Rol;
import com.gym.gym_app.models.Usuario;
import com.gym.gym_app.repository.AsistenciaRepository;
import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    /**
     * Clientas que llevan {@code dias} días o más sin asistir (o que nunca han asistido).
     * Ordenadas de mayor a menor inactividad; las que nunca han venido van primero.
     */
    public List<Map<String, Object>> reporteInasistencia(int dias) {
        if (dias < 1) {
            throw new IllegalArgumentException("El número de días debe ser mayor o igual a 1");
        }

        LocalDate hoy = LocalDate.now();

        // usuarioId -> fecha de su última entrada
        Map<Long, LocalDate> ultimaEntrada = new HashMap<>();
        for (Object[] fila : asistenciaRepository.findUltimaEntradaPorUsuario()) {
            ultimaEntrada.put((Long) fila[0], ((LocalDateTime) fila[1]).toLocalDate());
        }

        // usuarioId -> vencimiento de su membresía vigente
        Map<Long, LocalDate> vencimientos = new HashMap<>();
        for (Membresia m : membresiaRepository.findByActivaTrue()) {
            if (!m.getFechaVencimiento().isBefore(hoy)) {
                vencimientos.put(m.getUsuario().getId(), m.getFechaVencimiento());
            }
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.getRol() != Rol.CLIENTE) continue;

            LocalDate ultima = ultimaEntrada.get(u.getId());
            Long diasSin = ultima == null ? null : ChronoUnit.DAYS.between(ultima, hoy);

            // Se incluye si nunca ha asistido o si ya pasaron al menos 'dias' días
            if (diasSin != null && diasSin < dias) continue;

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("usuarioId", u.getId());
            fila.put("nombre", u.getNombre());
            fila.put("telefono", u.getTelefono());
            fila.put("ultimaAsistencia", ultima);          // null = nunca ha asistido
            fila.put("diasSinAsistir", diasSin);           // null = nunca ha asistido
            fila.put("membresiaActiva", vencimientos.containsKey(u.getId()));
            fila.put("fechaVencimiento", vencimientos.get(u.getId()));
            resultado.add(fila);
        }

        // Nunca asistieron primero; después, de más a menos días sin asistir
        resultado.sort((a, b) -> {
            Long da = (Long) a.get("diasSinAsistir");
            Long db = (Long) b.get("diasSinAsistir");
            if (da == null && db == null) return 0;
            if (da == null) return -1;
            if (db == null) return 1;
            return Long.compare(db, da);
        });

        return resultado;
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