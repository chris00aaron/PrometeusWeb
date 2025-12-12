package com.prometeus.prometeus.repository.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prometeus.prometeus.model.AuditoriaInicioSesion;

@Repository
public interface AuditoriaInicioSesionRepository extends JpaRepository<AuditoriaInicioSesion, Long> {

}
