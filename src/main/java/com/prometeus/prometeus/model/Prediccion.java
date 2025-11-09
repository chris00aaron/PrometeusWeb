package com.prometeus.prometeus.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "predicciones")
public class Prediccion {
    @Id
    @GeneratedValue
    private UUID uuid;

    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal ambiente;

    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal refrigeracion;

    @Column(name = "voltaje_d",precision = 10, scale = 3, nullable = false)
    private BigDecimal voltajeD;

    @Column(name = "voltaje_q",precision = 10, scale = 3, nullable = false)
    private BigDecimal voltajeQ;

    @Column(nullable = false)
    private Integer velocidad;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal torque;

    @Column(name = "corriente_d", precision = 10, scale = 3, nullable = false)
    private BigDecimal corrienteD;

    @Column(name = "corriente_q", precision = 10, scale = 3, nullable = false)
    private BigDecimal corrienteQ;

    @Column(name = "temperatura", precision = 6, scale = 2, nullable = false)
    private BigDecimal temperatura;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usuario", // Nombre de la columna FK en la tabla usuario
            referencedColumnName = "id", // Nombre de la columna referenciada en clase Usuario
            foreignKey = @ForeignKey(name = "fk_prediccion_usuario"), // Nombre del constraint generado por el ForeignKey
            nullable = false) // No acepta nulos
    private Usuario usuario;

    @PrePersist
    private void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }
}
