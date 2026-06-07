package com.gym.gym_app.controller;

import com.gym.gym_app.models.DatosCuerpo;
import com.gym.gym_app.service.DatosCuerpoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api/datos-cuerpo")
@CrossOrigin(origins = "*")
public class DatosCuerpoController {

    private final DatosCuerpoService datosCuerpoService;

    public DatosCuerpoController(DatosCuerpoService datosCuerpoService) {
        this.datosCuerpoService = datosCuerpoService;
    }

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public ResponseEntity<DatosCuerpo> guardar(@PathVariable Long usuarioId,
                                               @RequestBody DatosCuerpo datos) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(datosCuerpoService.guardar(usuarioId, datos));
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public List<DatosCuerpo> historial(@PathVariable Long usuarioId) {
        return datosCuerpoService.getHistorial(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/ultimo")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#usuarioId)")
    public ResponseEntity<DatosCuerpo> ultimo(@PathVariable Long usuarioId) {
        return datosCuerpoService.getUltimo(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}