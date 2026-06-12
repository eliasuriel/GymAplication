package com.gym.gym_app.controller;

import com.gym.gym_app.dto.UsuarioRequest;
import com.gym.gym_app.models.Rol;
import com.gym.gym_app.models.Usuario;
import com.gym.gym_app.security.JwtUtil;
import com.gym.gym_app.security.LoginRequest;
import com.gym.gym_app.security.LoginResponse;
import com.gym.gym_app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return usuarioService.getUsuarioByCorreo(request.getCorreo())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(u -> {
                    String token = jwtUtil.generarToken(u.getCorreo(), u.getRol().name());
                    return ResponseEntity.ok((Object) new LoginResponse(token, u.getCorreo(), u.getRol().name(), u.getId()));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas"));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody UsuarioRequest request) {
        if (usuarioService.existeCorreo(request.getCorreo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(request.getPassword());
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(request.getRol() != null ? request.getRol() : Rol.CLIENTE);

        Usuario nuevo = usuarioService.saveUsuario(usuario);
        String token = jwtUtil.generarToken(nuevo.getCorreo(), nuevo.getRol().name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, nuevo.getCorreo(), nuevo.getRol().name(), nuevo.getId()));
    }
}