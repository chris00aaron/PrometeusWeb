package com.prometeus.prometeus.repository.auditoria;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prometeus.prometeus.model.AuditoriaPredicciones;

@Repository
public interface AuditoriaPrediccionesRepository extends JpaRepository<AuditoriaPredicciones, Long> {
    Page<AuditoriaPredicciones> findByFechaPrediccionBetweenAndUsernameContainingIgnoreCaseAndRolContainingIgnoreCase(
            LocalDateTime start, LocalDateTime end, String username, String rol, Pageable pageable);
}
