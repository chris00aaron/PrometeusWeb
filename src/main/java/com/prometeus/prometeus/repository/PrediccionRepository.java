package com.prometeus.prometeus.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    long count();

    @Query("SELECT AVG(p.temperatura) FROM Prediccion p")
    BigDecimal findAverageTemperatura();

    @Query("SELECT p.usuario.username, COUNT(p.usuario.id) " +
           "FROM Prediccion p " +
           "GROUP BY p.usuario.username " +
           "ORDER BY COUNT(p.usuario.id) DESC " +
           "LIMIT 5")
    List<Object[]> findTop5ActiveUsers();

    @Query("SELECT AVG(p.voltajeD), AVG(p.voltajeQ), AVG(p.corrienteD), AVG(p.corrienteQ), AVG(p.velocidad) FROM Prediccion p")
    List<Object[]> findAverageInputParameters();
}
