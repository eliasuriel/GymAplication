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

    // Composición corporal
    private Double peso;
    private Double altura;
    private Double imc;
    private String categoria;
    private Double musculo;
    private Double grasaCorporal;
    private Double grasaVisceral;
    private Integer edadMetabolica;

    // Medidas corporales
    private Double busto;
    private Double cintura;
    private Double abdomen;
    private Double cadera;
    private Double muslo;
    private Double brazo;

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
    public Double getImc() { return imc; }
    public void setImc(Double imc) { this.imc = imc; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Double getMusculo() { return musculo; }
    public void setMusculo(Double musculo) { this.musculo = musculo; }
    public Double getGrasaCorporal() { return grasaCorporal; }
    public void setGrasaCorporal(Double grasaCorporal) { this.grasaCorporal = grasaCorporal; }
    public Double getGrasaVisceral() { return grasaVisceral; }
    public void setGrasaVisceral(Double grasaVisceral) { this.grasaVisceral = grasaVisceral; }
    public Integer getEdadMetabolica() { return edadMetabolica; }
    public void setEdadMetabolica(Integer edadMetabolica) { this.edadMetabolica = edadMetabolica; }
    public Double getBusto() { return busto; }
    public void setBusto(Double busto) { this.busto = busto; }
    public Double getCintura() { return cintura; }
    public void setCintura(Double cintura) { this.cintura = cintura; }
    public Double getAbdomen() { return abdomen; }
    public void setAbdomen(Double abdomen) { this.abdomen = abdomen; }
    public Double getCadera() { return cadera; }
    public void setCadera(Double cadera) { this.cadera = cadera; }
    public Double getMuslo() { return muslo; }
    public void setMuslo(Double muslo) { this.muslo = muslo; }
    public Double getBrazo() { return brazo; }
    public void setBrazo(Double brazo) { this.brazo = brazo; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}