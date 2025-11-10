package com.prometeus.prometeus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.prometeus.prometeus.dto.ApiResponse;
import com.prometeus.prometeus.dto.LoginRequest;
import com.prometeus.prometeus.dto.LoginResponse;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.service.UsuarioService;

@Slf4j
@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "/login", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        log.info("Login attempt: {}", request.getUsername());

        try {
            if (!usuarioService.validarCredenciales(request.getUsername(), request.getClave())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Usuario o contraseña inválidos"));
            }

            Usuario usuario = usuarioService.findByUsername(request.getUsername()).get();

            LoginResponse loginResponse = new LoginResponse(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getRol().toString()
            );

            log.info("Login exitoso: {}", request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", loginResponse));

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error en login"));
        }
    }
}
