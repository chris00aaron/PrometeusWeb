package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.prometeus.prometeus.model.AuditoriaAccesos;
import com.prometeus.prometeus.model.AuditoriaInicioSesion;
import com.prometeus.prometeus.repository.PrediccionRepository;
import com.prometeus.prometeus.repository.UsuarioRepository;
import com.prometeus.prometeus.repository.auditoria.AuditoriaAccesosRepository;
import com.prometeus.prometeus.repository.auditoria.AuditoriaInicioSesionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    
    // Repositorios inyectados para obtener los datos
    private final UsuarioRepository usuarioRepository;
    private final PrediccionRepository prediccionRepository;
    private final AuditoriaInicioSesionRepository inicioSesionRepository;
    private final AuditoriaAccesosRepository accesosRepository;

    /**
     * Recopila todas las métricas de resumen para el dashboard.
     */
    public Map<String, Object> getSummaryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Métrica 1: Total de Usuarios Registrados
        metrics.put("totalUsuarios", usuarioRepository.count());
        
        // Métrica 2: Total de Predicciones Históricas
        metrics.put("totalPredicciones", prediccionRepository.count());
        
        // Métrica 3: Último Inicio de Sesión Auditado
        Optional<AuditoriaInicioSesion> ultimoAcceso = inicioSesionRepository.findTopByOrderByFechaInicioSesionDesc();
        metrics.put("ultimoAcceso", ultimoAcceso.orElse(null));
        
        // Métrica 4: Temperatura Media Predicha Global
        BigDecimal avgTemp = prediccionRepository.findAverageTemperatura();
        metrics.put("tempMedia", (avgTemp != null) ? avgTemp.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return metrics;
    }
    
    /**
     * Obtiene el listado de los 5 usuarios con más predicciones.
     */
    public List<Object[]> getTopActiveUsers() {
        return prediccionRepository.findTop5ActiveUsers();
    }
    
    /**
     * Obtiene el listado de las últimas 5 creaciones de usuarios.
     */
    public List<AuditoriaAccesos> getLastUserCreations() {
        return accesosRepository.findTop5ByOrderByFechaCreacionUsuarioDesc();
    }

    /**
     * Obtiene los valores promedio de los parámetros de entrada clave y los formatea para la gráfica.
     * @return Lista de Object[] con [String Parameter Name, Double Average Value]
     */
    public List<Object[]> getAverageInputParameters() {
        List<Object[]> rawDataList = prediccionRepository.findAverageInputParameters();
        List<Object[]> formattedData = new ArrayList<>();
        
        if (rawDataList.isEmpty() || rawDataList.get(0) == null) {
            return formattedData;
        }
        
        Object[] rawData = rawDataList.get(0);
        
        if (rawData != null && rawData.length > 0 && rawData[0] != null) {
            
            String[] parameterNames = {"Voltaje D", "Voltaje Q", "Corriente D", "Corriente Q", "Velocidad"};
            
            for (int i = 0; i < rawData.length; i++) {
                if (rawData[i] != null) {
                    BigDecimal avgValue = (BigDecimal) rawData[i];
                    
                    formattedData.add(new Object[]{
                        parameterNames[i], 
                        avgValue.setScale(2, RoundingMode.HALF_UP).doubleValue()
                    });
                }
            }
        }
        
        return formattedData;
    }
}
