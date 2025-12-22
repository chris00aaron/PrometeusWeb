package com.prometeus.prometeus.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrediccionRequest {
@NotNull(message = "El campo 'ambiente' no puede ser nulo")
    @Digits(integer=3, fraction=2, message = "El campo 'ambiente' debe tener máximo 3 enteros y 2 decimales")
    @DecimalMin(value = "-50.0", message = "Temperatura mínima ambiente es {value}°C")
    @DecimalMax(value = "100.0", message = "Temperatura máxima ambiente es {value}°C")
    private BigDecimal ambiente;

    @NotNull(message = "El campo 'coolant' no puede ser nulo")
    @Digits(integer=3, fraction=2, message = "El campo 'coolant' debe tener máximo 3 enteros y 2 decimales")
    @Positive(message = "El campo 'coolant' debe ser un número positivo")
    private BigDecimal coolant;
    
    @NotNull(message = "El campo 'u_d' no puede ser nulo")
    @Digits(integer=4, fraction=3, message = "El campo 'u_d' debe tener máximo 4 enteros y 3 decimales")
    private BigDecimal u_d;
    
    @NotNull(message = "El campo 'u_q' no puede ser nulo")
    @Digits(integer=4, fraction=3, message = "El campo 'u_q' debe tener máximo 4 enteros y 3 decimales")
    private BigDecimal u_q;
    
    @NotNull(message = "El campo 'motor_speed' no puede ser nulo")
    @Positive(message = "La velocidad del motor debe ser un número positivo")
    private Integer motor_speed;
    
    @NotNull(message = "El campo 'i_d' no puede ser nulo")
    @Digits(integer=4, fraction=3, message = "El campo 'i_d' debe tener máximo 4 enteros y 3 decimales")
    private BigDecimal i_d;
    
    @NotNull(message = "El campo 'i_q' no puede ser nulo")
    @Digits(integer=4, fraction=3, message = "El campo 'i_q' debe tener máximo 4 enteros y 3 decimales")
    private BigDecimal i_q;
}
