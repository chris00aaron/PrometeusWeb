package com.prometeus.prometeus.security;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.UsuarioRepository;
import com.prometeus.prometeus.service.AuditoriaService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor

@Component
@Slf4j
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        usuario.setUltimaConexion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        auditoriaService.registrarInicioSesion(usuario);

        UsuarioDetallesSecurity us = new UsuarioDetallesSecurity(usuario);
        return us;
    }
}
