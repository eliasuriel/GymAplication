package com.gym.gym_app.scheduler;

import com.gym.gym_app.models.Membresia;
import com.gym.gym_app.repository.MembresiaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MembresiaScheduler {

    private static final Logger log = LoggerFactory.getLogger(MembresiaScheduler.class);

    private final MembresiaRepository membresiaRepository;

    public MembresiaScheduler(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    // Corre todos los días a las 00:01 AM
    @Scheduled(cron = "0 1 0 * * *")
    public void verificarVencimientos() {
        List<Membresia> vencidas = membresiaRepository
                .findByActivaTrueAndFechaVencimientoBefore(LocalDate.now());

        if (vencidas.isEmpty()) {
            log.info("No hay membresías vencidas hoy.");
            return;
        }

        vencidas.forEach(m -> m.setActiva(false));
        membresiaRepository.saveAll(vencidas);

        log.info("Se marcaron {} membresías como vencidas.", vencidas.size());
    }
}