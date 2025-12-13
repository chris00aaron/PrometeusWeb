package com.prometeus.prometeus.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "auditoria_predicciones")
public class AuditoriaPredicciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String clave;
    private String rol;
    private Double ambiente;
    private Double coolant;
    private Double u_d;
    private Double u_q;
    private Double i_d;
    private Double i_q;
    private Integer motor_speed;
    private Double temperatura;

    @jakarta.persistence.Column(name = "fecha_prediccion")
    private LocalDateTime fechaPrediccion;
}
