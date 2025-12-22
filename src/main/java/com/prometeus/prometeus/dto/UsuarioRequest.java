package com.prometeus.prometeus.dto;

import com.prometeus.prometeus.model.Rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String clave;

    @NotNull
    private Rol rol;
}