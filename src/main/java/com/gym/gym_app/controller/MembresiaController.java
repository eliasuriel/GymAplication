package com.gym.gym_app.controller;

import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.service.MembresiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Membresia> crear(@PathVariable Long usuarioId,
                                           @RequestBody Membresia membresia) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membresiaService.crearMembresia(usuarioId, membresia));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        membresiaService.deleteMembresia(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    public List<Membresia> historial(@PathVariable Long usuarioId) {
        return membresiaService.getHistorialByUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/activa")
    public ResponseEntity<Membresia> activa(@PathVariable Long usuarioId) {
        return membresiaService.getMembresiaActiva(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/verificar-vencimientos")
    public ResponseEntity<String> verificarVencimientos() {
        int cantidad = membresiaService.verificarYMarcarVencidas();
        return ResponseEntity.ok("Se marcaron " + cantidad + " membresías como vencidas.");
    }
}