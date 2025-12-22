package com.prometeus.prometeus.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prometeus.prometeus.service.MonitoreoService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RecepcionIotTask {
    
    private final MonitoreoService monitoreoService;

    @Scheduled(fixedRate = 60000) // Ejecutar cada minuto
    public void ejecutarMonitoreo() {
        monitoreoService.registrarMonitoreo();
    }
}
