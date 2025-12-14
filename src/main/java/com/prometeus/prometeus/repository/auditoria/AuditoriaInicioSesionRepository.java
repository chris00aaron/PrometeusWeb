package com.prometeus.prometeus.repository.auditoria;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.prometeus.prometeus.model.AuditoriaInicioSesion;

@Repository
public interface AuditoriaInicioSesionRepository extends JpaRepository<AuditoriaInicioSesion, Long> {
    Page<AuditoriaInicioSesion> findByFechaInicioSesionBetweenAndUsernameContainingIgnoreCaseAndRolContainingIgnoreCase(
            LocalDateTime start, LocalDateTime end, String username, String rol, Pageable pageable);

    
            
    Optional<AuditoriaInicioSesion> findTopByOrderByFechaInicioSesionDesc();
}
