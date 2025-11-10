package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.PrediccionRepository;
import com.prometeus.prometeus.repository.UsuarioRepository;
import java.util.List;

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
    public BigDecimal getPredictionAndSave(PrediccionRequest dto) {

        // 1. LLAMAR A LA API DE PYTHON (Simulación)
        // Aquí iría tu lógica con RestTemplate o WebClient
        // ResponseEntity<String> response = restTemplate.postForEntity("http://api-python/predict", dto, String.class);
        // BigDecimal temperaturaPredicha = new BigDecimal(response.getBody());
        
        // ---- Simulación ----
        // Simulamos una temperatura basada en el torque y el ambiente
        BigDecimal tempBase = new BigDecimal("65.0");
        BigDecimal torqueFactor = dto.getTorque().divide(new BigDecimal("10.0"), 2, RoundingMode.HALF_UP);
        BigDecimal ambientFactor = dto.getAmbiente().divide(new BigDecimal("20.0"), 2, RoundingMode.HALF_UP);
        BigDecimal temperaturaPredicha = tempBase.add(torqueFactor).add(ambientFactor);
        // --- Fin Simulación ---

        // 2. BUSCAR UN USUARIO (Para pruebas)
        // Como no tienes login, buscamos un usuario fijo (ej: ID 1).
        // ¡ASEGÚRATE DE QUE ESTE USUARIO EXISTA EN TU BD PARA PROBAR! (Ver paso 5)
        Usuario usuarioPrueba = usuarioRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Usuario de prueba ID 1 no encontrado."));

        // 3. CONSTRUIR LA ENTIDAD PREDICCION
        Prediccion nuevaPrediccion = Prediccion.builder()
                .ambiente(dto.getAmbiente())
                .refrigeracion(dto.getRefrigeracion())
                .voltajeD(dto.getVoltajeD())
                .voltajeQ(dto.getVoltajeQ())
                .velocidad(dto.getVelocidad())
                .torque(dto.getTorque())
                .corrienteD(dto.getCorrienteD())
                .corrienteQ(dto.getCorrienteQ())
                .temperatura(temperaturaPredicha) // El resultado de la API
                .usuario(usuarioPrueba)           // El usuario de prueba
                // 'uuid' y 'fechaCreacion' se manejan automáticamente
                .build();

        // 4. GUARDAR EN LA BASE DE DATOS
        prediccionRepository.save(nuevaPrediccion);

        // 5. DEVOLVER EL RESULTADO
        return temperaturaPredicha;
    }

    public List<Prediccion> getHistoryForUser(Long userId) {
        return prediccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(userId);
    }

}
