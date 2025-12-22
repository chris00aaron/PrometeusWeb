package com.prometeus.prometeus.repository.auditoria;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prometeus.prometeus.model.AuditoriaAutoEntrenamiento;

@Repository
public interface AuditoriaAutoentrenamientoRepository extends JpaRepository<AuditoriaAutoEntrenamiento, Long> {

    Optional<AuditoriaAutoEntrenamiento> findFirstByOrderByIdDesc();
}