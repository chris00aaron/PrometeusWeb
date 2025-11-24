package com.prometeus.prometeus.model;

import java.time.LocalDateTime;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder

@EqualsAndHashCode


@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    private String username;

    @Column(nullable = false, length = 120)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @Column(nullable = false)
    private LocalDateTime ultimaConexion;

    @Column
    private boolean activo;

    @OneToMany(fetch = FetchType.LAZY, // Carga perezosa para optimizar el rendimiento
            mappedBy = "usuario", // Atributo en la entidad Prediccion que mapea esta relación
            cascade = CascadeType.ALL) // Si se actualiza o elimina el padre los hijos tambien lo haran en cascada)
    private List<Prediccion> predicciones;

    @PrePersist
    private void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        fechaCreacion = ahora;
        fechaUltimaActualizacion = ahora;
        activo = true;
    }

    @PreUpdate
    private void preUpdate() {
        fechaUltimaActualizacion = LocalDateTime.now();
    }
}