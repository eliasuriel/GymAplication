package com.gym.gym_app.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuarios")
public class Usuario {

    // El ID lo asigna el admin al crear al usuario (si no se manda, el service asigna el siguiente)
    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(name = "foto")
    private String foto;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Relación 1:to many con la membresía
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Membresia> membresias;

    // Relación 1:N con datos corporales
    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<DatosCuerpo> datosCuerpo;

    // Constructor vacío
    public Usuario() {}

    // ===== Getters y Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Membresia> getMembresias() {
        return membresias;
    }
    public void setMembresias(List<Membresia> membresias) {
        this.membresias = membresias;
    }

    public List<DatosCuerpo> getDatosCuerpo() {
        return datosCuerpo;
    }

    public void setDatosCuerpo(List<DatosCuerpo> datosCuerpo) {
        this.datosCuerpo = datosCuerpo;
    }

    public String getFoto(){
        return foto;
    }

    public void setFoto(String foto){
        this.foto = foto;
    }
}