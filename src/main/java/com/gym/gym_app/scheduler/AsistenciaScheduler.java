package com.gym.gym_app.scheduler;

import com.gym.gym_app.service.AsistenciaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AsistenciaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaScheduler.class);
    private final AsistenciaService asistenciaService;

    public AsistenciaScheduler(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // Corre cada 15 minutos
    @Scheduled(fixedRate = 900000)
    public void verificarSalidasPendientes() {
        log.info("Verificando salidas automáticas...");
        asistenciaService.registrarSalidasAutomaticas();
    }
}