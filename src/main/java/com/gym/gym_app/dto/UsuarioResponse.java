package com.gym.gym_app.dto;

import com.gym.gym_app.models.Rol;

import java.time.LocalDate;

public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private Rol rol;
    private String foto;
    private LocalDate fechaNacimiento;

    public UsuarioResponse(Long id, String nombre, String correo, String telefono, Rol rol, String foto, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.foto = foto;
        this.fechaNacimiento = fechaNacimiento;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
}