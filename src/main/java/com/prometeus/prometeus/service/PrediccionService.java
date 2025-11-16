package com.prometeus.prometeus.service;

<<<<<<< Updated upstream
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
=======
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


>>>>>>> Stashed changes
import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.dto.PrediccionResponse;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.PrediccionRepository;
<<<<<<< Updated upstream
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
=======
import com.prometeus.prometeus.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import org.json.JSONObject;

@AllArgsConstructor
>>>>>>> Stashed changes

@Service
@Slf4j
public class PrediccionService {

<<<<<<< Updated upstream
    @Autowired
    private PrediccionRepository prediccionRepository;
=======
    @Value("${service.prediction.base-url}")
    private String baseUrl;

    @Value("${service.prediction.path}")
    private String predictionPath;
>>>>>>> Stashed changes

    public PrediccionResponse crearPrediccion(Usuario usuario, PrediccionRequest request) {
        log.info("Creando predicción para usuario: {}", usuario.getUsername());

<<<<<<< Updated upstream
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
=======
        // 1. LLAMAR A LA API DE PYTHON (Simulación)
        // Aquí iría tu lógica con RestTemplate o WebClient
        RestTemplate restTemplate = new RestTemplate();
        String urlCompleto = baseUrl + predictionPath;
        ResponseEntity<String> response = restTemplate.postForEntity(urlCompleto, dto, String.class);
        
        JSONObject json = new JSONObject(response.getBody());
        BigDecimal temperaturaPredicha = json
            .getJSONObject("temperatura_predicha")
            .getBigDecimal("_Output__stator_winding");

        temperaturaPredicha = temperaturaPredicha.setScale(2, RoundingMode.HALF_UP);
        
        // 2. BUSCAR UN USUARIO (Para pruebas)
        // Como no tienes login, buscamos un usuario fijo (ej: ID 1).
        // ¡ASEGÚRATE DE QUE ESTE USUARIO EXISTA EN TU BD PARA PROBAR! (Ver paso 5)
        Usuario usuarioPrueba = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario de prueba ID 1 no encontrado."));

        // 3. CONSTRUIR LA ENTIDAD PREDICCION
        Prediccion nuevaPrediccion = Prediccion.builder()
                .ambiente(dto.getAmbiente())
                .refrigeracion(dto.getCoolant())
                .voltajeD(dto.getU_d())
                .voltajeQ(dto.getU_q())
                .velocidad(dto.getMotor_speed())
                .torque(dto.getTorque())
                .corrienteD(dto.getI_d())
                .corrienteQ(dto.getI_q())
                .temperatura(temperaturaPredicha) // El resultado de la API
                .usuario(usuarioPrueba)           // El usuario de prueba
                // 'uuid' y 'fechaCreacion' se manejan automáticamente
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

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
=======
}
>>>>>>> Stashed changes
