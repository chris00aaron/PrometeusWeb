package com.prometeus.prometeus.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.Prediccion;

public interface PrediccionRepository extends JpaRepository<Prediccion,UUID> {

}
