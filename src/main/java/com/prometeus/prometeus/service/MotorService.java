package com.prometeus.prometeus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.prometeus.prometeus.model.EstadoMotor;
import com.prometeus.prometeus.model.Motor;
import com.prometeus.prometeus.repository.MotorRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@Service
public class MotorService {

    private final MotorRepository motorRepository;

    public List<Motor> obtenerMotoresActivos() {
        return motorRepository.findByEstado(EstadoMotor.FUNCIONANDO);
    }
}
