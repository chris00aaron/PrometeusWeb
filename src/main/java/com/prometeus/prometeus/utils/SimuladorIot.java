package com.prometeus.prometeus.utils;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class SimuladorIot {

    private Random random = new Random();

    // Rango de valores realistas para cada sensor
    private static final BigDecimal MIN_TEMPERATURA_AMBIENTE = new BigDecimal("20");
    private static final BigDecimal MAX_TEMPERATURA_AMBIENTE = new BigDecimal("150");
    
    private static final BigDecimal MIN_VOLTAGE = new BigDecimal("200");
    private static final BigDecimal MAX_VOLTAGE = new BigDecimal("600");

    private static final BigDecimal MAX_CORRIENTE = new BigDecimal("100");

    private static final Integer MIN_VELOCIDAD = 1000;  // 1000 RPM
    private static final Integer MAX_VELOCIDAD = 3000;  // 3000 RPM
    
    private static final BigDecimal MIN_REFRIGERACION = new BigDecimal("20");
    private static final BigDecimal MAX_REFRIGERACION = new BigDecimal("60");

    // Genera valores aleatorios para la temperatura
    public BigDecimal generarTemperaturaAmbiente() {
        return MIN_TEMPERATURA_AMBIENTE.add(BigDecimal.valueOf(random.nextDouble() * (MAX_TEMPERATURA_AMBIENTE.doubleValue() - MIN_TEMPERATURA_AMBIENTE.doubleValue())));
    }

    // Genera un voltaje aleatorio entre un rango razonable
    public BigDecimal generarVoltaje() {
        return MIN_VOLTAGE.add(BigDecimal.valueOf(random.nextDouble() * (MAX_VOLTAGE.doubleValue() - MIN_VOLTAGE.doubleValue())));
    }

    // Genera una corriente aleatoria que depende del voltaje y la velocidad
    public BigDecimal generarCorriente(BigDecimal voltaje, Integer velocidad) {
        // Relacionar corriente con voltaje y velocidad (ejemplo simplificado)
        BigDecimal baseCorriente = BigDecimal.valueOf(5 + random.nextDouble() * (voltaje.doubleValue() / 100));
        
        // A mayor velocidad, mayor corriente
        if (velocidad > 2000) {
            baseCorriente = baseCorriente.add(BigDecimal.valueOf(10));
        }
        
        return baseCorriente.min(MAX_CORRIENTE);
    }

    // Genera una velocidad del motor entre los límites
    public Integer generarVelocidad() {
        return MIN_VELOCIDAD + random.nextInt(MAX_VELOCIDAD - MIN_VELOCIDAD + 1);
    }

    // Genera el valor de la refrigeración, que depende de la temperatura y carga
    public BigDecimal generarRefrigeracion(BigDecimal temperaturaAmbiente) {
        // A mayor temperatura, mayor refrigeración necesaria
        BigDecimal baseRefrigeracion = MIN_REFRIGERACION.add(BigDecimal.valueOf(random.nextDouble() * (MAX_REFRIGERACION.doubleValue() - MIN_REFRIGERACION.doubleValue())));
        
        if (temperaturaAmbiente.doubleValue() > 80) {
            baseRefrigeracion = baseRefrigeracion.add(BigDecimal.valueOf(10));  // Aumento de refrigeración si la temperatura es alta
        }
        
        return baseRefrigeracion;
    }
}

