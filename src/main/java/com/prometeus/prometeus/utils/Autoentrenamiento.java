package com.prometeus.prometeus.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prometeus.prometeus.service.AuditoriaAutoentrenamientoService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor

@Component
public class Autoentrenamiento {
    private final AuditoriaAutoentrenamientoService auditoriaAutoentrenamientoService;
    // Ejecutar domingo a las 12 de la noche
    @Scheduled(cron = "0 0 0 * * SUN", zone = "America/Lima")
    public void triggerAutoentrenamiento() {
        try {
            if (!auditoriaAutoentrenamientoService.cumpleCondicionesAutoentrenamiento()) {
                log.info("No se cumple con las condiciones para el autoentrenamiento");
                return;
            }
            auditoriaAutoentrenamientoService.registrarAutoentrenamiento();   
        } catch (Exception e) {
            log.error("Error al ejecutar el autoentrenamiento", e);
        }
    }
}