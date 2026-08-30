package com.gym.gym_app.security;

public class LoginResponse {
    private String token;
    private String correo;
    private String rol;
    private Long id;
    private String foto;

    public LoginResponse(String token, String correo, String rol, Long id, String foto) {
        this.token = token;
        this.correo = correo;
        this.rol = rol;
        this.id = id;
        this.foto = foto;
    }

    public String getToken() { return token; }
    public String getCorreo() { return correo; }
    public String getRol() { return rol; }
    public Long getId() { return id; }
    public String getFoto(){return foto;}
}