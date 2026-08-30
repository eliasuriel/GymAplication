package com.gym.gym_app.controller;

import com.gym.gym_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/fotos")
@CrossOrigin(origins = "*")
public class FotoController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final UsuarioService usuarioService;

    public FotoController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> subirFoto(
            @PathVariable Long usuarioId,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        // Validar que sea una imagen
        String contentType = foto.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Solo se permiten archivos de imagen");
        }

        // Crear carpeta si no existe
        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Eliminar foto anterior si existe
        usuarioService.getUsuarioById(usuarioId).ifPresent(u -> {
            if (u.getFoto() != null) {
                try {
                    Files.deleteIfExists(Paths.get(uploadDir).resolve(u.getFoto()));
                } catch (IOException ignored) {}
            }
        });

        // Generar nombre único
        String extension = foto.getOriginalFilename()
                .substring(foto.getOriginalFilename().lastIndexOf('.'));
        String nombreArchivo = "usuario_" + usuarioId + "_" + UUID.randomUUID() + extension;

        // Guardar archivo
        Path rutaArchivo = dirPath.resolve(nombreArchivo);
        Files.copy(foto.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        // Guardar nombre en el usuario
        usuarioService.actualizarFoto(usuarioId, nombreArchivo);

        return ResponseEntity.ok(Map.of(
                "nombreArchivo", nombreArchivo,
                "url", "/api/fotos/" + nombreArchivo
        ));
    }

    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerFoto(@PathVariable String nombreArchivo) throws IOException {
        Path rutaArchivo = Paths.get(uploadDir).resolve(nombreArchivo);
        Resource resource = new UrlResource(rutaArchivo.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(rutaArchivo);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg"))
                .body(resource);
    }

    @DeleteMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarFoto(@PathVariable Long usuarioId) {
        usuarioService.getUsuarioById(usuarioId).ifPresent(u -> {
            if (u.getFoto() != null) {
                try {
                    Files.deleteIfExists(Paths.get(uploadDir).resolve(u.getFoto()));
                    usuarioService.actualizarFoto(usuarioId, null);
                } catch (IOException ignored) {}
            }
        });
        return ResponseEntity.ok(Map.of("mensaje", "Foto eliminada correctamente"));
    }
}