package com.prometeus.prometeus.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.EstadoMotor;
import com.prometeus.prometeus.model.Monitoreo;
import com.prometeus.prometeus.model.Motor;
import com.prometeus.prometeus.repository.MonitoreoRepository;
import com.prometeus.prometeus.repository.MotorRepository;
import com.prometeus.prometeus.utils.SimuladorIot;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@Service
public class MonitoreoService {

    private final MonitoreoRepository monitoreoRepository;
    private final PrediccionService prediccionService;
    private final SimuladorIot simuladorSensor;
    private final MotorRepository motorRepository;

    // Método para generar monitoreos de todos los motores disponibles
    public void registrarMonitoreo() {
        // 1. Obtener la lista de todos los motores disponibles
        List<Motor> motores = motorRepository.findByEstado(EstadoMotor.FUNCIONANDO);
        
        // 2. Si no hay motores disponibles, no se generan monitoreos
        if (motores.isEmpty()) return;

        // 3. Generar valores aleatorios para cada motor
        for (Motor motor : motores) {
            // Generar los valores aleatorios de los sensores
            BigDecimal ambiente = simuladorSensor.generarTemperaturaAmbiente();
            BigDecimal voltaje = simuladorSensor.generarVoltaje();
            Integer velocidad = simuladorSensor.generarVelocidad();
            BigDecimal corriente = simuladorSensor.generarCorriente(voltaje, velocidad);
            BigDecimal refrigeracion = simuladorSensor.generarRefrigeracion(ambiente);

            //Consultar a la API
            BigDecimal temperatura = prediccionService.getPrediction(
                PrediccionRequest.builder()
                .ambiente(ambiente)
                .coolant(refrigeracion)
                .u_d(voltaje)
                .u_q(voltaje)
                .i_d(corriente)
                .i_q(corriente)
                .motor_speed(velocidad)
                .build());

            // 4. Crear un objeto de Monitoreo
            Monitoreo monitoreo = Monitoreo.builder()
                .ambiente(ambiente)
                .refrigeracion(refrigeracion)
                .voltajeD(voltaje)
                .voltajeQ(voltaje) 
                .corrienteD(corriente)
                .corrienteQ(corriente) 
                .temperatura(temperatura)
                .velocidad(velocidad)
                .motor(motor) 
                .build();

            // 5. Guardar el monitoreo en la base de datos
            monitoreoRepository.save(monitoreo);
        }
    }

    public List<Monitoreo> obtenerMonitoreosUltimosMinutos(Long motorId, int minutos) {
        LocalDateTime tiempoLimite = LocalDateTime.now().minusMinutes(minutos);
        return monitoreoRepository.findMonitoreosUltimosMinutos(motorId, tiempoLimite);
    }
}
