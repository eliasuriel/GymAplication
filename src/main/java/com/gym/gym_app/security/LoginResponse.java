package com.gym.gym_app.security;

public class LoginResponse {
    private String token;
    private String correo;
    private String rol;
    private Long id;

    public LoginResponse(String token, String correo, String rol, Long id) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.id = id;
    }

    public String getToken() { return token; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public Long getId() { return id; }
}