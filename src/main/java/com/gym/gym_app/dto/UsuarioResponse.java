package com.gym.gym_app.dto;

import com.gym.gym_app.models.Rol;

public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private Rol rol;
    private String foto;

    public UsuarioResponse(Long id, String nombre, String correo, String telefono, Rol rol,String foto) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.foto = foto;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }
    public String getTelefono() {
        return telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public String getFoto(){
        return foto;
    }
}