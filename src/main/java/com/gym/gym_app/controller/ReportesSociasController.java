package com.gym.gym_app.controller;

import com.gym.gym_app.models.Asistencia;
import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.models.Rol;
import com.gym.gym_app.models.Usuario;
import com.gym.gym_app.repository.AsistenciaRepository;
import com.gym.gym_app.repository.MembresiaRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes-socias")
@PreAuthorize("hasRole('ADMIN')")
public class ReportesSociasController {
    private final UsuarioRepository usuarios;
    private final AsistenciaRepository asistencias;
    private final MembresiaRepository membresias;

    public ReportesSociasController(UsuarioRepository usuarios, AsistenciaRepository asistencias,
                                    MembresiaRepository membresias) {
        this.usuarios = usuarios;
        this.asistencias = asistencias;
        this.membresias = membresias;
    }

    public record Fila(Long numeroSocia, String nombre, LocalDate vencimiento,
                       LocalDate ultimaAsistencia, LocalDate cumpleanos) {}

    @GetMapping
    public Map<String, Object> generar(@RequestParam LocalDate desde,
                                       @RequestParam LocalDate hasta,
                                       @RequestParam(required = false) Integer mes) {
        LocalDate hoy = LocalDate.now();
        if (desde.isAfter(hasta) || hasta.isAfter(hoy) || desde.isBefore(hoy.minusYears(5))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Selecciona un rango válido, sin fechas futuras y de máximo 5 años atrás");
        }
        int mesCumple = mes == null ? hoy.getMonthValue() : mes;
        if (mesCumple < 1 || mesCumple > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes debe estar entre 1 y 12");
        }

        // Las entradas definen quién asistió en el periodo seleccionado.
        // Reporte 1: se verifica cobertura en cada asistencia y se excluyen renovadas actualmente.
        // Reporte 3: se verifica vigencia actual.
        List<Asistencia> entradas = asistencias.findByFechaEntradaBetween(
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay().minusNanos(1));
        Map<Long, List<Asistencia>> entradasPorSocia = entradas.stream()
                .filter(a -> a.getUsuario() != null && a.getFechaEntrada() != null)
                .collect(Collectors.groupingBy(a -> a.getUsuario().getId()));
        Map<Long, List<Membresia>> membresiasPorSocia = membresias.findAll().stream()
                .filter(m -> m.getUsuario() != null && m.getFechaPago() != null && m.getFechaVencimiento() != null)
                .collect(Collectors.groupingBy(m -> m.getUsuario().getId()));

        List<Fila> vencidas = new ArrayList<>();
        List<Fila> cumpleanos = new ArrayList<>();
        List<Fila> vigentes = new ArrayList<>();
        for (Usuario socia : usuarios.findAll()) {
            if (socia.getRol() != Rol.CLIENTE) continue;
            List<Asistencia> susEntradas = entradasPorSocia.getOrDefault(socia.getId(), List.of());
            if (susEntradas.isEmpty()) continue;
            List<Membresia> susMembresias = membresiasPorSocia.getOrDefault(socia.getId(), List.of());
            LocalDate ultimaEntrada = susEntradas.stream()
                    .map(a -> a.getFechaEntrada().toLocalDate()).max(LocalDate::compareTo).orElse(null);

            // Regla del reporte 1: detectar una asistencia sin membresía vigente
            // EN LA FECHA REAL DE ESA ASISTENCIA, no por el vencimiento de hoy.
            // Si la socia ya renovó y tiene una membresía activa actualmente,
            // se regularizó: ya no aparece en vencidas y pasa al reporte 3.
            Optional<Membresia> vigenteHoy = membresiaVigenteEn(susMembresias, hoy);
            if (vigenteHoy.isPresent()) {
                Membresia vigente = vigenteHoy.get();
                vigentes.add(new Fila(socia.getId(), socia.getNombre(), vigente.getFechaVencimiento(),
                        ultimaEntrada, socia.getFechaNacimiento()));
            } else {
                Optional<LocalDate> ultimaEntradaSinCobertura = susEntradas.stream()
                        .map(a -> a.getFechaEntrada().toLocalDate())
                        .filter(diaAsistencia -> membresiaVigenteEn(susMembresias, diaAsistencia).isEmpty())
                        .max(LocalDate::compareTo);
                if (ultimaEntradaSinCobertura.isPresent()) {
                    // Mostramos el vencimiento de su última membresía ya vencida
                    // a la fecha del reporte (o null si jamás tuvo membresía).
                    LocalDate vencimiento = ultimoVencimientoPrevio(susMembresias, hoy).orElse(null);
                    vencidas.add(new Fila(socia.getId(), socia.getNombre(), vencimiento,
                            ultimaEntradaSinCobertura.get(), socia.getFechaNacimiento()));
                }
            }

            // Reporte 2: asistió en el periodo y cumple años en el mes elegido;
            // no depende del estado de su membresía.
            if (socia.getFechaNacimiento() != null &&
                    socia.getFechaNacimiento().getMonthValue() == mesCumple) {
                cumpleanos.add(new Fila(socia.getId(), socia.getNombre(), null,
                        ultimaEntrada, socia.getFechaNacimiento()));
            }

        }

        Comparator<Fila> porApellido = Comparator
                .comparing((Fila f) -> claveApellido(f.nombre()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(f -> normalizar(f.nombre()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Fila::numeroSocia);
        vencidas.sort(porApellido);
        cumpleanos.sort(Comparator.comparing((Fila f) -> f.cumpleanos().getDayOfMonth()).thenComparing(porApellido));
        vigentes.sort(porApellido);

        Map<String, Object> salida = new LinkedHashMap<>();
        salida.put("vencidas", vencidas);
        salida.put("cumpleanos", cumpleanos);
        salida.put("vigentes", vigentes);
        salida.put("desde", desde);
        salida.put("hasta", hasta);
        salida.put("mes", mesCumple);
        salida.put("fechaEmision", hoy);
        return salida;
    }

    private static Optional<Membresia> membresiaVigenteEn(List<Membresia> ms, LocalDate dia) {
        return ms.stream()
                .filter(m -> !m.getFechaPago().isAfter(dia) && !m.getFechaVencimiento().isBefore(dia))
                .max(Comparator.comparing(Membresia::getFechaVencimiento));
    }

    private static Optional<LocalDate> ultimoVencimientoPrevio(List<Membresia> ms, LocalDate dia) {
        return ms.stream()
                .filter(m -> m.getFechaVencimiento().isBefore(dia) && !m.getFechaPago().isAfter(dia))
                .map(Membresia::getFechaVencimiento)
                .max(LocalDate::compareTo);
    }

    // Aproximación: el esquema actual guarda el nombre completo en una sola columna.
    private static String claveApellido(String nombre) {
        if (nombre == null || nombre.isBlank()) return "";
        String[] partes = normalizar(nombre).split("\\s+");
        return partes.length >= 3 ? partes[partes.length - 2] :
                partes.length == 2 ? partes[1] : partes[0];
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }
}
