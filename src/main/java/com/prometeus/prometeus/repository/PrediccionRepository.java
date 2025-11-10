package com.prometeus.prometeus.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;

public interface PrediccionRepository extends JpaRepository<Prediccion, UUID> {
    List<Prediccion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
}
