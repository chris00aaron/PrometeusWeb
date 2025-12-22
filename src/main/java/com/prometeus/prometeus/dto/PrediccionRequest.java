package com.prometeus.prometeus.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrediccionRequest {
    private BigDecimal ambiente;
    private BigDecimal coolant;
    private BigDecimal u_d;
    private BigDecimal u_q;
    private Integer motor_speed;
    private BigDecimal torque;
    private BigDecimal i_d;
    private BigDecimal i_q;
}
