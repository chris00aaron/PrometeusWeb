package com.prometeus.prometeus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.dto.PrediccionResponse;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.PrediccionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrediccionService {

    @Autowired
    private PrediccionRepository prediccionRepository;

    public PrediccionResponse crearPrediccion(Usuario usuario, PrediccionRequest request) {
        log.info("Creando predicción para usuario: {}", usuario.getUsername());

        BigDecimal temperatura = calcularTemperatura(request);

        Prediccion prediccion = Prediccion.builder()
                .ambiente(request.getAmbiente())
                .refrigeracion(request.getRefrigeracion())
                .voltajeD(request.getVoltajeD())
                .voltajeQ(request.getVoltajeQ())
                .velocidad(request.getVelocidad())
                .torque(request.getTorque())
                .corrienteD(request.getCorrienteD())
                .corrienteQ(request.getCorrienteQ())
                .temperatura(temperatura)
                .usuario(usuario)
                .build();

        Prediccion saved = prediccionRepository.save(prediccion);
        log.info("Predicción guardada con ID: {}", saved.getUuid());

        return mapToResponse(saved);
    }

    private BigDecimal calcularTemperatura(PrediccionRequest request) {
        BigDecimal baseTemp = BigDecimal.valueOf(30)
                .add(BigDecimal.valueOf(request.getVelocidad()).divide(BigDecimal.valueOf(100)))
                .add(request.getTorque().multiply(BigDecimal.valueOf(5)));
        
        return baseTemp.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public List<PrediccionResponse> obtenerHistorial(Usuario usuario) {
        log.info("Obteniendo historial para: {}", usuario.getUsername());
        List<Prediccion> predicciones = prediccionRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
        return predicciones.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PrediccionResponse mapToResponse(Prediccion prediccion) {
        return new PrediccionResponse(
                prediccion.getUuid(),
                prediccion.getAmbiente(),
                prediccion.getRefrigeracion(),
                prediccion.getVoltajeD(),
                prediccion.getVoltajeQ(),
                prediccion.getVelocidad(),
                prediccion.getTorque(),
                prediccion.getCorrienteD(),
                prediccion.getCorrienteQ(),
                prediccion.getTemperatura(),
                prediccion.getFechaCreacion()
        );
    }
}