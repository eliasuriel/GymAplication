package com.gym.gym_app.controller;

import com.gym.gym_app.dto.AsistenciaManualRequest;
import com.gym.gym_app.models.Asistencia;
import com.gym.gym_app.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping("/entrada/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public ResponseEntity<Asistencia> entrada(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asistenciaService.registrarEntrada(usuarioId));
    }

    @PostMapping("/manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Asistencia> registrarManual(@Valid @RequestBody AsistenciaManualRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asistenciaService.registrarAsistenciaManual(
                        request.getUsuarioId(), request.getFecha(), request.getHora()));
    }

    @PutMapping("/salida/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public ResponseEntity<Asistencia> salida(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(asistenciaService.registrarSalida(usuarioId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaService.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public List<Asistencia> historial(@PathVariable Long usuarioId) {
        return asistenciaService.getHistorial(usuarioId);
    }

    @GetMapping("/reporte/inasistencia")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> reporteInasistencia(@RequestParam(defaultValue = "7") int dias) {
        return asistenciaService.reporteInasistencia(dias);
    }

    @GetMapping("/hoy")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Asistencia> hoy() {
        return asistenciaService.getAsistenciasHoy();
    }
}