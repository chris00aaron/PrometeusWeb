package com.prometeus.prometeus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.EstadoMotor;
import com.prometeus.prometeus.model.Motor;

public interface MotorRepository extends JpaRepository<Motor, Long> {

    List<Motor> findByEstado(EstadoMotor estado);
}
