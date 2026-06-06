package com.gym.gym_app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "datos_cuerpo")
public class DatosCuerpo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    private Double peso;      // kg
    private Double altura;    // metros
    private Double cintura;   // cm (opcional)
    private Double imc;
    private String categoria;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @PrePersist
    @PreUpdate
    public void calcularImc() {
        if (peso != null && altura != null && altura > 0) {
            this.imc = Math.round((peso / (altura * altura)) * 100.0) / 100.0;
            this.categoria = interpretarImc(this.imc);
        }
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }

    private String interpretarImc(double imc) {
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25.0) return "Peso normal";
        if (imc < 30.0) return "Sobrepeso";
        if (imc < 35.0) return "Obesidad grado I";
        if (imc < 40.0) return "Obesidad grado II";
        return "Obesidad grado III";
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Double getPeso() { return peso; }

    public void setPeso(Double peso) { this.peso = peso; }

    public Double getAltura() { return altura; }

    public void setAltura(Double altura) { this.altura = altura; }

    public Double getCintura() { return cintura; }

    public void setCintura(Double cintura) { this.cintura = cintura; }

    public Double getImc() { return imc; }

    public void setImc(Double imc) { this.imc = imc; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }

    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}