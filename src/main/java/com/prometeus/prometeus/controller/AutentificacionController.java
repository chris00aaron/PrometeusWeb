package com.prometeus.prometeus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prometeus.prometeus.dto.LoginDTO;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/usuario")
public class AutentificacionController {
    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String getLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String validarLogin(@ModelAttribute("loginDTO") LoginDTO loginDTO,HttpSession sesion, Model model) {
        log.info("DTO : {}", loginDTO);

        if (!usuarioService.validarCredenciales(loginDTO.getUsername(), loginDTO.getClave())) {
            model.addAttribute("error", "Usuario no encontrado");
            return "login";
        }

        Usuario usuario = usuarioService.findByUsername(loginDTO.getUsername()).get();
        sesion.setAttribute("user", usuario);
        return "redirect:/predict";
    }
}