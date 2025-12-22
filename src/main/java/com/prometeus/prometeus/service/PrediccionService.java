package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.PrediccionRepository;
import com.prometeus.prometeus.repository.UsuarioRepository;
import java.util.List;
import org.json.JSONObject;


@Service
public class PrediccionService {
    private final PrediccionRepository prediccionRepository;
    private final UsuarioRepository usuarioRepository;

    public PrediccionService(PrediccionRepository prediccionRepository, UsuarioRepository usuarioRepository) {
        this.prediccionRepository = prediccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Procesa los datos del DTO, llama a la API, guarda la predicción y devuelve el resultado.
     */
    public BigDecimal getPredictionAndSave(PrediccionRequest dto, Long userId) {

        // 1. LLAMAR A LA API DE PYTHON (Simulación)
        // Aquí iría tu lógica con RestTemplate o WebClient
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://4d089a912bec.ngrok-free.app/predecir/", dto, String.class);
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
                .build();

        // 4. GUARDAR EN LA BASE DE DATOS
        prediccionRepository.save(nuevaPrediccion);

        // 5. DEVOLVER EL RESULTADO
        return temperaturaPredicha;
    }

    public BigDecimal getPrediction(PrediccionRequest dto) {

        // 1. LLAMAR A LA API DE PYTHON
        // Aquí iría tu lógica con RestTemplate o WebClient
        RestTemplate restTemplate = new RestTemplate();
        String urlCompleto = baseUrl + predictionPath;
        ResponseEntity<String> response = restTemplate.postForEntity(urlCompleto, dto, String.class);

        JSONObject json = new JSONObject(response.getBody());
        BigDecimal temperaturaPredicha = json
                .getJSONObject("temperatura_predicha")
                .getBigDecimal("_Output__stator_winding");

        temperaturaPredicha = temperaturaPredicha.setScale(2, RoundingMode.HALF_UP);

        // 2. DEVOLVER EL RESULTADO
        return temperaturaPredicha;
    }

    public BigDecimal getPrediction(PrediccionRequest dto) {

        // 1. LLAMAR A LA API DE PYTHON
        // Aquí iría tu lógica con RestTemplate o WebClient
        RestTemplate restTemplate = new RestTemplate();
        String urlCompleto = baseUrl + predictionPath;
        ResponseEntity<String> response = restTemplate.postForEntity(urlCompleto, dto, String.class);

        JSONObject json = new JSONObject(response.getBody());
        BigDecimal temperaturaPredicha = json
                .getJSONObject("temperatura_predicha")
                .getBigDecimal("_Output__stator_winding");

        temperaturaPredicha = temperaturaPredicha.setScale(2, RoundingMode.HALF_UP);

        // 2. DEVOLVER EL RESULTADO
        return temperaturaPredicha;
    }

    public List<Prediccion> getHistoryForUser(Long userId) {
        return prediccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
    }

}
