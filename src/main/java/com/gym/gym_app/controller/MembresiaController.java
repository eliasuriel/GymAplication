package com.gym.gym_app.controller;

import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.service.MembresiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@CrossOrigin(origins = "*")
public class MembresiaController {

    private final MembresiaService membresiaService;

    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    // 🔹 Obtener membresías de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<Membresia> getMembresias(@PathVariable Long usuarioId) {
        return membresiaService.getMembresiasByUsuario(usuarioId);
    }

    // 🔹 Crear / renovar membresía
    @PostMapping
    public Membresia crearMembresia(@RequestBody Membresia membresia) {
        return membresiaService.saveMembresia(membresia);
    }
}

