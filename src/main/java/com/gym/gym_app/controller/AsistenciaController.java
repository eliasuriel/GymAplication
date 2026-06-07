package com.gym.gym_app.controller;

import com.gym.gym_app.models.Asistencia;
import com.gym.gym_app.service.AsistenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping("/entrada/{usuarioId}")
    public ResponseEntity<Asistencia> entrada(@PathVariable Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asistenciaService.registrarEntrada(usuarioId));
    }

    @PutMapping("/salida/{usuarioId}")
    public ResponseEntity<Asistencia> salida(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(asistenciaService.registrarSalida(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    public List<Asistencia> historial(@PathVariable Long usuarioId) {
        return asistenciaService.getHistorial(usuarioId);
    }

    @GetMapping("/hoy")
    public List<Asistencia> hoy() {
        return asistenciaService.getAsistenciasHoy();
    }
}