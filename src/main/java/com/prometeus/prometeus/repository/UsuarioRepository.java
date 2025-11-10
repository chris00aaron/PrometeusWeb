package com.prometeus.prometeus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
