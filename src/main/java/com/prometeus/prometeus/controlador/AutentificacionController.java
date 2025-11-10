package com.prometeus.prometeus.controlador;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/usuario")
public class AutentificacionController {
    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String getLogin(Model modal) {
        modal.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String validarLogin(@ModelAttribute LoginDTO loginDTO, RedirectAttributes redirectAttrs,HttpSession sesion, Model model) {
        log.info("DTO : {}", loginDTO);

        if (!usuarioService.validarCredenciales(loginDTO.getUsername(), loginDTO.getClave())) {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:login";
        }
        Usuario usuario = usuarioService.findByUsername(loginDTO.getUsername()).get();
        sesion.setAttribute("user", usuario);
        log.info("Usuario Guardado : {}", usuario);
        return "redirect:/predict";
    }
}