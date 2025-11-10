package com.prometeus.prometeus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.prometeus.prometeus.dto.ApiResponse;
import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.dto.PrediccionResponse;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.service.PrediccionService;
import com.prometeus.prometeus.service.UsuarioService;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/predicciones")
public class PrediccionController {

    @Autowired
    private PrediccionService prediccionService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "/{userId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ApiResponse<PrediccionResponse>> crearPrediccion(
            @PathVariable Long userId,
            @RequestBody PrediccionRequest request) {

        log.info("Solicitud de predicción para usuario: {}", userId);

        try {
            Usuario usuario = usuarioService.findById(userId);
            PrediccionResponse response = prediccionService.crearPrediccion(usuario, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Predicción realizada", response));

        } catch (Exception e) {
            log.error("Error en predicción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping(value = "/historial/{userId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<PrediccionResponse>>> obtenerHistorial(
            @PathVariable Long userId) {

        log.info("Solicitando historial para usuario: {}", userId);

        try {
            Usuario usuario = usuarioService.findById(userId);
            List<PrediccionResponse> historial = prediccionService.obtenerHistorial(usuario);
            return ResponseEntity.ok(ApiResponse.success("Historial obtenido", historial));

        } catch (Exception e) {
            log.error("Error obteniendo historial: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
