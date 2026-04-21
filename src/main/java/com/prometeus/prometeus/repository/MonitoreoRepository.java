package com.prometeus.prometeus.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prometeus.prometeus.model.Monitoreo;

public interface MonitoreoRepository extends JpaRepository<Monitoreo, Long> {

    @Query("SELECT m FROM Monitoreo m WHERE m.motor.id = :motorId AND m.fechaIngreso >= :tiempoLimite")
    List<Monitoreo> findMonitoreosUltimosMinutos(@Param("motorId") Long motorId,
            @Param("tiempoLimite") LocalDateTime tiempoLimite);
}
