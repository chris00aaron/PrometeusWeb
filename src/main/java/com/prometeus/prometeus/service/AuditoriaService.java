package com.prometeus.prometeus.service;

import com.prometeus.prometeus.repository.auditoria.AuditoriaInicioSesionRepository;
import com.prometeus.prometeus.repository.auditoria.AuditoriaPrediccionesRepository;
import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.AuditoriaAccesos;
import com.prometeus.prometeus.model.AuditoriaInicioSesion;
import com.prometeus.prometeus.model.AuditoriaPredicciones;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.auditoria.AuditoriaAccesosRepository;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaService {
    private final AuditoriaInicioSesionRepository inicioSesionRepo;
    private final AuditoriaPrediccionesRepository prediccionesRepo;
    private final AuditoriaAccesosRepository accesosRepo;

    // Registrar inicio de sesión
    public void registrarInicioSesion(Usuario usuario) {
        AuditoriaInicioSesion inicioSesion = AuditoriaInicioSesion.builder()
                .username(usuario.getUsername())
                .clave(usuario.getClave())
                .rol(usuario.getRol().toString())
                .fechaInicioSesion(LocalDateTime.now())
                .build();
        inicioSesionRepo.save(inicioSesion);
    }

    // Registrar predicción
    public void registrarPrediccion(Usuario usuario, PrediccionRequest dto, BigDecimal temperatura) {
        AuditoriaPredicciones predicciones = AuditoriaPredicciones.builder()
                .username(usuario.getUsername())
                .clave(usuario.getClave())
                .rol(usuario.getRol().toString())
                .fechaPrediccion(LocalDateTime.now())
                .ambiente(dto.getAmbiente().doubleValue())
                .coolant(dto.getCoolant().doubleValue())
                .u_d(dto.getU_d().doubleValue())
                .u_q(dto.getU_q().doubleValue())
                .motor_speed(dto.getMotor_speed())
                .i_d(dto.getI_d().doubleValue())
                .i_q(dto.getI_q().doubleValue())
                .temperatura(temperatura.doubleValue())
                .build();
        prediccionesRepo.save(predicciones);
    }

    // Registrar creación de usuario
    public void registrarCreacionUsuario(Usuario admin, Usuario nuevoUsuario) {
        AuditoriaAccesos accesos = AuditoriaAccesos.builder()
                .usernameAdmin(admin.getUsername())
                .usernameNuevo(nuevoUsuario.getUsername())
                .claveNueva(nuevoUsuario.getClave())
                .rolNuevo(nuevoUsuario.getRol().toString())
                .fechaCreacionUsuario(LocalDateTime.now())
                .build();
        accesosRepo.save(accesos);
    }
}