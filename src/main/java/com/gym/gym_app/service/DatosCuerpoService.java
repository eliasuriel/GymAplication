package com.gym.gym_app.service;

import com.gym.gym_app.models.DatosCuerpo;
import com.gym.gym_app.repository.DatosCuerpoRepository;
import com.gym.gym_app.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    public List<Map<String, Object>> reporteProgresoMensual() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(1);

        List<DatosCuerpo> registros = datosCuerpoRepository
                .findByFechaRegistroBetween(inicio, fin);

        // Agrupar por usuario
        Map<Long, List<DatosCuerpo>> porUsuario = registros.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        d -> d.getUsuario().getId()
                ));

        List<Map<String, Object>> resultado = new java.util.ArrayList<>();

        porUsuario.forEach((usuarioId, datos) -> {
            if (datos.size() < 2) return; // Necesita al menos 2 registros para comparar

            // Ordenar por fecha — el más reciente primero
            datos.sort((a, b) -> b.getFechaRegistro().compareTo(a.getFechaRegistro()));

            DatosCuerpo reciente = datos.get(0);
            DatosCuerpo anterior = datos.get(datos.size() - 1);

            Map<String, Object> progreso = new java.util.LinkedHashMap<>();
            progreso.put("usuarioId", usuarioId);
            progreso.put("nombre", reciente.getUsuario().getNombre());
            progreso.put("correo", reciente.getUsuario().getCorreo());
            progreso.put("fechaInicio", anterior.getFechaRegistro());
            progreso.put("fechaFin", reciente.getFechaRegistro());
            progreso.put("registros", datos.size());

            // Diferencias
            if (reciente.getPeso() != null && anterior.getPeso() != null)
                progreso.put("difPeso", Math.round((reciente.getPeso() - anterior.getPeso()) * 10.0) / 10.0);
            if (reciente.getGrasaCorporal() != null && anterior.getGrasaCorporal() != null)
                progreso.put("difGrasa", Math.round((reciente.getGrasaCorporal() - anterior.getGrasaCorporal()) * 10.0) / 10.0);
            if (reciente.getMusculo() != null && anterior.getMusculo() != null)
                progreso.put("difMusculo", Math.round((reciente.getMusculo() - anterior.getMusculo()) * 10.0) / 10.0);
            if (reciente.getCintura() != null && anterior.getCintura() != null)
                progreso.put("difCintura", Math.round((reciente.getCintura() - anterior.getCintura()) * 10.0) / 10.0);
            if (reciente.getCadera() != null && anterior.getCadera() != null)
                progreso.put("difCadera", Math.round((reciente.getCadera() - anterior.getCadera()) * 10.0) / 10.0);

            resultado.add(progreso);
        });

        // Ordenar por mayor baja de peso
        resultado.sort((a, b) -> {
            Double pa = (Double) a.getOrDefault("difPeso", 0.0);
            Double pb = (Double) b.getOrDefault("difPeso", 0.0);
            return Double.compare(pa, pb);
        });

        return resultado;
    }
}