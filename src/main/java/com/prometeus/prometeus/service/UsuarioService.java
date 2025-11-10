package com.prometeus.prometeus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.UsuarioRepository;
import java.util.Optional;

@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public boolean validarCredenciales(String username, String clave) {
        Optional<Usuario> usuario = findByUsername(username);
        if (usuario.isEmpty()) {
            return false;
        }
        return usuario.get().getClave().equals(clave);
    }
}
