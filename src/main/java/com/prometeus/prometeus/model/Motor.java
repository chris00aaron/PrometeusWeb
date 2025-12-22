package com.prometeus.prometeus.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "motores")
public class Motor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Modelo del motor
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    // Potencia del motor (kilovatios).
    @Column(name = "potencia_nominal", nullable = false)
    private Double potenciaNominal;

    // Voltaje nominal del motor (volts).
    @Column(name = "voltaje_nominal", nullable = false)
    private Double voltajeNominal;

    // Corriente nominal del motor (amperios).
    @Column(name = "corriente_nominal", nullable = false)
    private Double corrienteNominal;

    // Velocidad nominal del motor (rpm).
    @Column(name = "velocidad_nominal", nullable = false)
    private Integer velocidadNominal;

    // Torque nominal del motor (kilogramos por metro).
    @Column(name = "torque_nominal", nullable = false)
    private Double torqueNominal;

    // Temperatura máxima de operación del motor (grados Celsius).
    @Column(name = "temperatura_maxima_operacion", nullable = false)
    private Double temperaturaMaximaOperacion;

    // Eficiencia del motor (porcentaje).
    @Column(name = "eficiencia", nullable = false)
    private Double eficiencia;

    // Número de polos del motor.
    @Column(name = "numero_polos", nullable = false)
    private Integer numeroPolos;

    // Material del rotor del motor.
    @Column(name = "material_rotor", nullable = false, length = 100)
    private String materialRotor;

    // Diámetro del motor (metros).
    @Column(name = "diametro", nullable = false)
    private Double diametro;

    // Longitud del motor (metros).
    @Column(name = "longitud", nullable = false)
    private Double longitud;

    // Fecha de fabricación del motor.
    @Column(name = "fecha_fabricacion", nullable = false)
    private Date fechaFabricacion;

    // Estado del motor (por ejemplo, "Funcionando", "En mantenimiento", "Desconectado", etc.).
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMotor estado;

    // Número de serie del motor.
    @Column(name = "numero_serie",unique = true, nullable = false, length = 40)
    private String numeroSerie;

    // Ubicación del motor.
    @Column(name = "ubicacion", nullable = false, length = 120)
    private String ubicacion;

    @OneToMany(fetch = FetchType.LAZY, // Carga perezosa para optimizar el rendimiento
            mappedBy = "motor", // Atributo en la entidad Prediccion que mapea esta relación
            cascade = CascadeType.ALL) // Si se actualiza o elimina el padre los hijos tambien lo haran en cascada)
    private List<Monitoreo> monitoreos;
}

