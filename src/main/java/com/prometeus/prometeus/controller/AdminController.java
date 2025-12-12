package com.prometeus.prometeus.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prometeus.prometeus.dto.UsuarioRequest;
import com.prometeus.prometeus.model.Rol;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.security.UsuarioDetallesSecurity;
import com.prometeus.prometeus.service.AuditoriaService;
import com.prometeus.prometeus.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("usuarioRequest", new UsuarioRequest());
        model.addAttribute("roles", Rol.values());
        return "crear-usuario";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(
            @Valid @ModelAttribute UsuarioRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioDetallesSecurity adminDetails,
            RedirectAttributes redirect,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "crear-usuario";
        }

        Usuario nuevoUsuario = usuarioService.crearUsuario(request, passwordEncoder);
        // Auditar la creación
        Usuario admin = usuarioService.findById(adminDetails.getUsuarioId());
        auditoriaService.registrarCreacionUsuario(admin, nuevoUsuario);
        redirect.addFlashAttribute("mensaje", "Usuario creado exitosamente");
        return "redirect:/admin/usuarios/nuevo";
    }
}