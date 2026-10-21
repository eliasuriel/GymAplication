package com.gym.gym_app.controller;

import com.gym.gym_app.models.Rol;
import com.gym.gym_app.security.JwtUtil;
import com.gym.gym_app.security.LoginRequest;
import com.gym.gym_app.security.LoginResponse;
import com.gym.gym_app.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
                    return ResponseEntity.ok((Object) new LoginResponse(token, u.getCorreo(), u.getRol().name(), u.getId(),u.getFoto()));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas"));
    }

    @PostMapping("/login-id")
    public ResponseEntity<?> loginConId(@RequestBody Map<String, Long> body) {
        Long usuarioId = body.get("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El ID es obligatorio");
        }

        return usuarioService.getUsuarioById(usuarioId)
                .filter(u -> u.getRol() == Rol.CLIENTE)
                .map(u -> {
                    String token = jwtUtil.generarToken(u.getCorreo(), u.getRol().name());
                    return ResponseEntity.ok((Object) new LoginResponse(
                            token, u.getCorreo(), u.getRol().name(), u.getId(), u.getFoto(), u.getFechaNacimiento(), u.getNombre()));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("ID de cliente no encontrado"));
    }
}