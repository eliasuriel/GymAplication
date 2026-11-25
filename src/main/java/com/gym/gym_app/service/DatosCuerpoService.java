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

        if (datos.getFechaRegistro() != null && datos.getFechaRegistro().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de medición no puede ser futura");
        }
        datos.setUsuario(usuarioRepository.findById(usuarioId).get());
        return datosCuerpoRepository.save(datos);
    }


    // Administración: actualizar solo los campos editables, sin cambiar el dueño del registro.
    @org.springframework.transaction.annotation.Transactional
    public DatosCuerpo actualizar(Long registroId, DatosCuerpo cambios) {
        DatosCuerpo registro = datosCuerpoRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de medidas no encontrado: " + registroId));
        if (cambios.getFechaRegistro() == null || cambios.getFechaRegistro().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de medición debe ser válida y no futura");
        }
        registro.setFechaRegistro(cambios.getFechaRegistro());
        registro.setPeso(cambios.getPeso());
        registro.setAltura(cambios.getAltura());
        registro.setMusculo(cambios.getMusculo());
        registro.setGrasaCorporal(cambios.getGrasaCorporal());
        registro.setGrasaVisceral(cambios.getGrasaVisceral());
        registro.setEdadMetabolica(cambios.getEdadMetabolica());
        registro.setBusto(cambios.getBusto());
        registro.setCintura(cambios.getCintura());
        registro.setAbdomen(cambios.getAbdomen());
        registro.setCadera(cambios.getCadera());
        registro.setMuslo(cambios.getMuslo());
        registro.setBrazo(cambios.getBrazo());
        return datosCuerpoRepository.save(registro);
    }

    @org.springframework.transaction.annotation.Transactional
    public void eliminar(Long registroId) {
        DatosCuerpo registro = datosCuerpoRepository.findById(registroId)
                .orElseThrow(() -> new EntityNotFoundException("Registro de medidas no encontrado: " + registroId));
        datosCuerpoRepository.delete(registro);
    }

    public List<DatosCuerpo> getHistorial(Long usuarioId) {
        return datosCuerpoRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    public Optional<DatosCuerpo> getUltimo(Long usuarioId) {
        return datosCuerpoRepository.findTopByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    /**
     * Ranking de pérdida de peso de los últimos 30 días naturales (incluye hoy).
     * Solo se incluyen socias con al menos dos mediciones de peso válidas,
     * realizadas en fechas diferentes, y cuya diferencia final - inicial es negativa.
     */
    public List<Map<String, Object>> reporteProgresoMensual() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusDays(29);

        List<DatosCuerpo> registros = datosCuerpoRepository.findByFechaRegistroBetween(inicio, fin);
        Map<Long, List<DatosCuerpo>> porUsuario = registros.stream()
                .collect(java.util.stream.Collectors.groupingBy(d -> d.getUsuario().getId()));
        List<Map<String, Object>> resultado = new java.util.ArrayList<>();

        porUsuario.forEach((usuarioId, datos) -> {
            // Orden cronológico, estable incluso con varios registros el mismo día.
            datos.sort(java.util.Comparator.comparing(DatosCuerpo::getFechaRegistro)
                    .thenComparing(DatosCuerpo::getId));

            List<DatosCuerpo> conPeso = datos.stream()
                    .filter(d -> d.getPeso() != null && Double.isFinite(d.getPeso()) && d.getPeso() > 0)
                    .collect(java.util.stream.Collectors.toList());
            if (conPeso.size() < 2) return;

            DatosCuerpo anterior = conPeso.get(0);
            DatosCuerpo reciente = conPeso.get(conPeso.size() - 1);
            // No comparar dos capturas del mismo día como si fuera progreso mensual.
            if (!anterior.getFechaRegistro().isBefore(reciente.getFechaRegistro())) return;

            double difPeso = Math.round((reciente.getPeso() - anterior.getPeso()) * 10.0) / 10.0;
            if (difPeso >= 0) return; // No incluir aumentos ni peso sin cambio.

            Map<String, Object> progreso = new java.util.LinkedHashMap<>();
            progreso.put("usuarioId", usuarioId);
            progreso.put("nombre", reciente.getUsuario().getNombre());
            progreso.put("correo", reciente.getUsuario().getCorreo());
            progreso.put("fechaInicio", anterior.getFechaRegistro());
            progreso.put("fechaFin", reciente.getFechaRegistro());
            progreso.put("registros", conPeso.size());
            progreso.put("difPeso", difPeso);

            // Para las demás medidas, comparar solo valores presentes dentro del
            // mismo intervalo de medición de peso. Nunca asumir que null equivale a 0.
            List<DatosCuerpo> intervalo = datos.stream()
                    .filter(d -> !d.getFechaRegistro().isBefore(anterior.getFechaRegistro())
                            && !d.getFechaRegistro().isAfter(reciente.getFechaRegistro()))
                    .collect(java.util.stream.Collectors.toList());
            agregarDiferencia(progreso, "difGrasa", intervalo, DatosCuerpo::getGrasaCorporal);
            agregarDiferencia(progreso, "difMusculo", intervalo, DatosCuerpo::getMusculo);
            agregarDiferencia(progreso, "difCintura", intervalo, DatosCuerpo::getCintura);
            agregarDiferencia(progreso, "difCadera", intervalo, DatosCuerpo::getCadera);

            resultado.add(progreso);
        });

        // Más kilos perdidos primero: -8.0 antes que -3.0.
        resultado.sort(java.util.Comparator.comparingDouble(m -> (Double) m.get("difPeso")));
        return resultado;
    }

    private void agregarDiferencia(Map<String, Object> progreso, String clave,
                                   List<DatosCuerpo> datos,
                                   java.util.function.Function<DatosCuerpo, Double> campo) {
        List<DatosCuerpo> valores = datos.stream()
                .filter(d -> campo.apply(d) != null && Double.isFinite(campo.apply(d)))
                .collect(java.util.stream.Collectors.toList());
        if (valores.size() < 2) return;
        DatosCuerpo primero = valores.get(0);
        DatosCuerpo ultimo = valores.get(valores.size() - 1);
        if (!primero.getFechaRegistro().isBefore(ultimo.getFechaRegistro())) return;
        double diferencia = Math.round((campo.apply(ultimo) - campo.apply(primero)) * 10.0) / 10.0;
        progreso.put(clave, diferencia);
    }
    /**
     * Dos rankings independientes, comparando EXCLUSIVAMENTE los dos últimos
     * registros de cada socia, ordenados por fecha y después por ID.
     * El Top de medidas suma la reducción neta en cm de busto, cintura,
     * abdomen, cadera, muslo y brazo (requiere ambos registros completos).
     */
    public Map<String, Object> reporteTop5() {
        Map<Long, List<DatosCuerpo>> porUsuario = datosCuerpoRepository.findAll().stream()
                .filter(d -> d.getUsuario() != null && d.getFechaRegistro() != null)
                .collect(java.util.stream.Collectors.groupingBy(d -> d.getUsuario().getId()));

        List<Map<String, Object>> topPeso = new java.util.ArrayList<>();
        List<Map<String, Object>> topMedidas = new java.util.ArrayList<>();

        porUsuario.forEach((usuarioId, registros) -> {
            registros.sort(java.util.Comparator.comparing(DatosCuerpo::getFechaRegistro)
                    .thenComparing(DatosCuerpo::getId));
            if (registros.size() < 2) return;
            DatosCuerpo anterior = registros.get(registros.size() - 2);
            DatosCuerpo ultimo = registros.get(registros.size() - 1);

            java.util.function.Supplier<Map<String, Object>> base = () -> {
                Map<String, Object> fila = new java.util.LinkedHashMap<>();
                fila.put("usuarioId", usuarioId);
                fila.put("nombre", ultimo.getUsuario().getNombre());
                fila.put("correo", ultimo.getUsuario().getCorreo());
                fila.put("fechaAnterior", anterior.getFechaRegistro());
                fila.put("fechaUltima", ultimo.getFechaRegistro());
                return fila;
            };

            if (medidaValida(anterior.getPeso()) && medidaValida(ultimo.getPeso())) {
                double diferenciaPeso = redondear(ultimo.getPeso() - anterior.getPeso());
                if (diferenciaPeso < 0) {
                    Map<String, Object> fila = base.get();
                    fila.put("pesoAnterior", anterior.getPeso());
                    fila.put("pesoUltimo", ultimo.getPeso());
                    fila.put("difPeso", diferenciaPeso);
                    topPeso.add(fila);
                }
            }

            Double[] antes = {anterior.getBusto(), anterior.getCintura(), anterior.getAbdomen(),
                    anterior.getCadera(), anterior.getMuslo(), anterior.getBrazo()};
            Double[] despues = {ultimo.getBusto(), ultimo.getCintura(), ultimo.getAbdomen(),
                    ultimo.getCadera(), ultimo.getMuslo(), ultimo.getBrazo()};
            boolean completos = true;
            for (int i = 0; i < antes.length; i++) {
                if (!medidaValida(antes[i]) || !medidaValida(despues[i])) {
                    completos = false;
                    break;
                }
            }
            if (completos) {
                double diferenciaTotal = 0;
                String[] claves = {"difBusto", "difCintura", "difAbdomen", "difCadera", "difMuslo", "difBrazo"};
                Map<String, Object> fila = base.get();
                for (int i = 0; i < antes.length; i++) {
                    double diferencia = redondear(despues[i] - antes[i]);
                    diferenciaTotal += diferencia;
                    fila.put(claves[i], diferencia);
                }
                diferenciaTotal = redondear(diferenciaTotal);
                if (diferenciaTotal < 0) {
                    fila.put("difTotalCm", diferenciaTotal);
                    topMedidas.add(fila);
                }
            }
        });

        topPeso.sort(java.util.Comparator.comparingDouble(m -> (Double) m.get("difPeso")));
        topMedidas.sort(java.util.Comparator.comparingDouble(m -> (Double) m.get("difTotalCm")));
        Map<String, Object> resultado = new java.util.LinkedHashMap<>();
        resultado.put("topPeso", topPeso.stream().limit(5).collect(java.util.stream.Collectors.toList()));
        resultado.put("topMedidas", topMedidas.stream().limit(5).collect(java.util.stream.Collectors.toList()));
        return resultado;
    }

    private boolean medidaValida(Double valor) {
        return valor != null && Double.isFinite(valor) && valor > 0;
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

}