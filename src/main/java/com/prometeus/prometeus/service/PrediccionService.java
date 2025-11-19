package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.model.dto.PrediccionRequest;
import com.prometeus.prometeus.repository.PrediccionRepository;
import com.prometeus.prometeus.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // <--- IMPORTANTE: Agregar este import

@RequiredArgsConstructor
@Slf4j // <--- IMPORTANTE: Agregar esta anotación para los logs
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
        
        // INICIO DEL BLOQUE TRY (T025)
        try {
            log.info("Iniciando proceso de predicción para usuario ID: {}", userId);

            // 1. LLAMAR A LA API DE PYTHON (Simulación)
            RestTemplate restTemplate = new RestTemplate();
            String urlCompleto = baseUrl + predictionPath;
            
            // Esta línea puede fallar si la API externa está caída
            ResponseEntity<String> response = restTemplate.postForEntity(urlCompleto, dto, String.class);
            
            // Validamos que el código sea 200 OK y que el cuerpo no venga vacío
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error en la respuesta de la API de predicción. Status: " + response.getStatusCode());
            }

            JSONObject json = new JSONObject(response.getBody());
            BigDecimal temperaturaPredicha = json
                .getJSONObject("temperatura_predicha")
                .getBigDecimal("_Output__stator_winding");

            temperaturaPredicha = temperaturaPredicha.setScale(2, RoundingMode.HALF_UP);
            
            // 2. BUSCAR UN USUARIO
            Usuario usuarioPrueba = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

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
                    .temperatura(temperaturaPredicha)
                    .usuario(usuarioPrueba)
                    .build();

            // 4. GUARDAR EN LA BASE DE DATOS
            prediccionRepository.save(nuevaPrediccion);

            log.info("Predicción realizada y guardada exitosamente. Temperatura: {}", temperaturaPredicha);
            
            // 5. DEVOLVER EL RESULTADO
            return temperaturaPredicha;

        } catch (Exception e) {
            // CAPTURA DE ERRORES (T025 y T028)
            log.error("Error crítico al procesar la predicción: {}", e.getMessage(), e);
            
            // Relanzamos una excepción genérica para que el Controller sepa que falló
            throw new RuntimeException("Error interno al procesar la predicción: " + e.getMessage());
        }
    }

    public List<Prediccion> getHistoryForUser(Long userId) {
        return prediccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
    }
}