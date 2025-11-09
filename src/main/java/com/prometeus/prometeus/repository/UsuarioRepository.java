package com.prometeus.prometeus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prometeus.prometeus.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
