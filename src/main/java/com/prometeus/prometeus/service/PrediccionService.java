package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.PrediccionRepository;
import com.prometeus.prometeus.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class PrediccionService {
    private final PrediccionRepository prediccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${service.prediction.base-url}")
    private String baseUrl;

    @Value("${service.prediction.path}")
    private String predictionPath;

    /**
     * Procesa los datos del DTO, llama a la API, guarda la predicción y devuelve el resultado.
     */
    public BigDecimal getPredictionAndSave(PrediccionRequest dto, Long userId) {

        // 1. LLAMAR A LA API DE PYTHON (Simulación)
        // Aquí iría tu lógica con RestTemplate o WebClient
        RestTemplate restTemplate = new RestTemplate();
        String urlCompleto = baseUrl+predictionPath;
        ResponseEntity<String> response = restTemplate.postForEntity(urlCompleto, dto, String.class);
        
        JSONObject json = new JSONObject(response.getBody());
        BigDecimal temperaturaPredicha = json
            .getJSONObject("temperatura_predicha")
            .getBigDecimal("_Output__stator_winding");

        temperaturaPredicha = temperaturaPredicha.setScale(2, RoundingMode.HALF_UP);
        
        // 2. BUSCAR UN USUARIO (Para pruebas)
        Usuario usuarioPrueba = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario de prueba ID 1 no encontrado."));

        // 3. CONSTRUIR LA ENTIDAD PREDICCION
        Prediccion nuevaPrediccion = Prediccion.builder()
                .ambiente(dto.getAmbiente())
                .refrigeracion(dto.getCoolant())
                .voltajeD(dto.getU_d())
                .voltajeQ(dto.getU_q())
                .velocidad(dto.getMotor_speed())
                .corrienteD(dto.getI_d())
                .corrienteQ(dto.getI_q())
                .temperatura(temperaturaPredicha) // El resultado de la API
                .usuario(usuarioPrueba)           // El usuario de prueba
                .build();

        // 4. GUARDAR EN LA BASE DE DATOS
        prediccionRepository.save(nuevaPrediccion);

        // 5. DEVOLVER EL RESULTADO
        return temperaturaPredicha;
    }

    public List<Prediccion> getHistoryForUser(Long userId) {
        return prediccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
    }

    public Page<Prediccion> getHistoryFiltered(Long userId, int page, LocalDate start, LocalDate end) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("fechaCreacion").descending());

        if (start != null && end != null) {
            return prediccionRepository.findByUsuarioIdAndFechaCreacionBetween(
                    userId,
                    start.atStartOfDay(),
                    end.atTime(23, 59),
                    pageable
            );
        }

        return prediccionRepository.findByUsuarioId(userId, pageable);
    }
}
