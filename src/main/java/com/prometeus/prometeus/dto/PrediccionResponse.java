package com.prometeus.prometeus.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrediccionResponse {
    private UUID uuid;
    private BigDecimal ambiente;
    private BigDecimal refrigeracion;
    private BigDecimal voltajeD;
    private BigDecimal voltajeQ;
    private Integer velocidad;
    private BigDecimal torque;
    private BigDecimal corrienteD;
    private BigDecimal corrienteQ;
    private BigDecimal temperatura;
    private LocalDateTime fechaCreacion;
}
