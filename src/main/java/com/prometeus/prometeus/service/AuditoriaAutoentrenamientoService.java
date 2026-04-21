package com.prometeus.prometeus.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prometeus.prometeus.model.AuditoriaAutoEntrenamiento;
import com.prometeus.prometeus.repository.auditoria.AuditoriaAutoentrenamientoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class AuditoriaAutoentrenamientoService {

    private final AuditoriaAutoentrenamientoRepository auditoriaAutoentrenamientoRepository;
    private final Integer PLAZO_ENTRE_AUTOENTRENAMIENTOS = 30;

    @Value("${service.prediction.base-url}")
    private String baseUrl;

    @Value("${service.prediction.path}")
    private String predictionPath;

    public boolean cumpleCondicionesAutoentrenamiento() {
        AuditoriaAutoEntrenamiento ultimoAutoentrenamiento = auditoriaAutoentrenamientoRepository
                .findFirstByOrderByIdDesc().orElse(null);
        if (ultimoAutoentrenamiento == null) {
            return true;
        }
        LocalDateTime fechaUltimoAutoentrenamiento = ultimoAutoentrenamiento.getFechaEntrenamiento();
        LocalDateTime fechaActual = LocalDateTime.now();
        Long diasTranscurridos = ChronoUnit.DAYS.between(fechaUltimoAutoentrenamiento, fechaActual);
        return diasTranscurridos >= PLAZO_ENTRE_AUTOENTRENAMIENTOS;
    }

    public void registrarAutoentrenamiento() {
        RestTemplate restTemplate = new RestTemplate();
        String urlCompleto = baseUrl + predictionPath;
        restTemplate.postForEntity(urlCompleto, null, String.class);
        auditoriaAutoentrenamientoRepository.save(new AuditoriaAutoEntrenamiento());
    }
}
