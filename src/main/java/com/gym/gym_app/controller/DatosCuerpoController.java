package com.gym.gym_app.controller;

import com.gym.gym_app.models.DatosCuerpo;
import com.gym.gym_app.service.DatosCuerpoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datos-cuerpo")
@CrossOrigin(origins = "*")
public class DatosCuerpoController {

    private final DatosCuerpoService datosCuerpoService;

    public DatosCuerpoController(DatosCuerpoService datosCuerpoService) {
        this.datosCuerpoService = datosCuerpoService;
    }

    // 🔹 Historial corporal del usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<DatosCuerpo> getHistorial(@PathVariable Long usuarioId) {
        return datosCuerpoService.getHistorial(usuarioId);
    }

    // 🔹 Registrar nuevo peso/medidas
    @PostMapping
    public DatosCuerpo guardarDatos(@RequestBody DatosCuerpo datosCuerpo) {
        return datosCuerpoService.guardarDatos(datosCuerpo);
    }
}

