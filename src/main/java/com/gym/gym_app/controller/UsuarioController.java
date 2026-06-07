package com.gym.gym_app.controller;

import com.gym.gym_app.dto.UsuarioRequest;
import com.gym.gym_app.dto.UsuarioResponse;
import com.gym.gym_app.models.Usuario;
import com.gym.gym_app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> getAll() {
        return usuarioService.getAllUsuarios()
                .stream()
                .map(usuarioService::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @usuarioService.getCorreoById(#id)")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(usuarioService::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con id " + id + " no encontrado"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(request.getPassword());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(request.getRol());
        return ResponseEntity.ok(usuarioService.toResponse(usuarioService.saveUsuario(usuario)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}