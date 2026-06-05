package com.gym.gym_app.dto;

import com.gym.gym_app.models.Rol;

public class UsuarioResponse {
    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private Rol rol;

    public UsuarioResponse(Integer id, String nombre, String correo, String telefono, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }

    public Integer getId() {
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
}