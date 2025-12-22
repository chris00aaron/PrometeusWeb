package com.prometeus.prometeus.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "monitoreos")
public class Monitoreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática de ID
    @Column(name = "id_monitoreo")
    private Long idMonitoreo;

    @Column(name = "ambiente", precision = 6, scale = 2)
    private BigDecimal ambiente;

    @JsonProperty("coolant")
    @Column(precision = 6, scale = 2, nullable = false)
    private BigDecimal refrigeracion;

    @JsonProperty("u_d")
    @Column(name = "voltaje_d", precision = 10, scale = 3, nullable = false)
    private BigDecimal voltajeD;

    @JsonProperty("u_q")
    @Column(name = "voltaje_q", precision = 10, scale = 3, nullable = false)
    private BigDecimal voltajeQ;

    @JsonProperty("motor_speed")
    @Column(nullable = false)
    private Integer velocidad;

    @JsonProperty("i_d")
    @Column(name = "corriente_d", precision = 10, scale = 3, nullable = false)
    private BigDecimal corrienteD;

    @JsonProperty("i_q")
    @Column(name = "corriente_q", precision = 10, scale = 3, nullable = false)
    private BigDecimal corrienteQ;

    @Column(name = "temperatura", precision = 6, scale = 2, nullable = false)
    private BigDecimal temperatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_motor", referencedColumnName = "id", 
                foreignKey = @ForeignKey(name = "fk_monitoreo_motor"), 
                nullable = false)
    private Motor motor;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @PrePersist
    private void prePersist() {
        fechaIngreso = LocalDateTime.now();
    }
}
