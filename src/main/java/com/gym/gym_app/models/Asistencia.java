package com.gym.gym_app.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "fecha_entrada", nullable = false)
    private LocalDateTime fechaEntrada;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @PrePersist
    public void setFechaEntrada() {
        if (this.fechaEntrada == null) {
            this.fechaEntrada = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id; }

    public void setId(Long id) {
        this.id = id; }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }
    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }
    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }
    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}