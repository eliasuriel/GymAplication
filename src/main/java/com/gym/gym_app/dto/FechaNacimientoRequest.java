package com.gym.gym_app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class FechaNacimientoRequest {

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }

    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}