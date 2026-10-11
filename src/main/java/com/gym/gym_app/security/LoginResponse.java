package com.gym.gym_app.security;

import java.time.LocalDate;

public class LoginResponse {
    private String token;
    private String correo;
    private String rol;
    private Long id;
    private String foto;
    private LocalDate fechaNacimiento;

    public LoginResponse(String token, String correo, String rol, Long id, String foto) {
        this(token, correo, rol, id, foto, null);
    }

    public LoginResponse(String token, String correo, String rol, Long id, String foto, LocalDate fechaNacimiento) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.id = id;
        this.foto = foto;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getToken() { return token; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public Long getId() { return id; }
    public String getFoto(){return foto;}
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
}