package com.prometeus.prometeus.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.Prediccion;

public interface PrediccionRepository extends JpaRepository<Prediccion,UUID> {
    List<Prediccion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    
    Page<Prediccion> findByUsuarioId(Long userId, Pageable pageable);

    Page<Prediccion> findByUsuarioIdAndFechaCreacionBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
