package com.prometeus.prometeus.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prometeus.prometeus.dto.UsuarioRequest;
import com.prometeus.prometeus.model.AuditoriaAccesos;
import com.prometeus.prometeus.model.AuditoriaInicioSesion;
import com.prometeus.prometeus.model.AuditoriaPredicciones;
import com.prometeus.prometeus.model.Rol;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.auditoria.AuditoriaAccesosRepository;
import com.prometeus.prometeus.repository.auditoria.AuditoriaInicioSesionRepository;
import com.prometeus.prometeus.repository.auditoria.AuditoriaPrediccionesRepository;
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

    // Repositorios de auditoría
    private final AuditoriaAccesosRepository accesosRepository;
    private final AuditoriaInicioSesionRepository sesionesRepository;
    private final AuditoriaPrediccionesRepository prediccionesRepository;

    private static final int PAGE_SIZE = 10;

    // =============================================
    // GESTIÓN DE USUARIOS
    // =============================================

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

    // =============================================
    // AUDITORÍAS
    // =============================================

    /**
     * Dashboard principal de auditorías
     */
    @GetMapping("/auditorias")
    public String mostrarAuditorias() {
        return "auditorias";
    }

    /**
     * Auditoría de creación de usuarios (accesos)
     */
    @GetMapping("/auditoria/accesos")
    public String mostrarAuditoriaAccesos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String rol,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Fechas por defecto: último mes
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "fechaCreacionUsuario"));
        Page<AuditoriaAccesos> accesosPage = accesosRepository
                .findByFechaCreacionUsuarioBetweenAndUsernameAdminContainingIgnoreCaseAndRolNuevoContainingIgnoreCase(
                        start, end, username, rol, pageable);

        model.addAttribute("accesos", accesosPage.getContent());
        model.addAttribute("accesosPage", accesosPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        model.addAttribute("rol", rol);
        model.addAttribute("roles", Rol.values());

        return "auditoria-accesos";
    }

    /**
     * Auditoría de inicios de sesión
     */
    @GetMapping("/auditoria/sesiones")
    public String mostrarAuditoriaSesiones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String rol,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Fechas por defecto: último mes
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "fechaInicioSesion"));
        Page<AuditoriaInicioSesion> sesionesPage = sesionesRepository
                .findByFechaInicioSesionBetweenAndUsernameContainingIgnoreCaseAndRolContainingIgnoreCase(
                        start, end, username, rol, pageable);

        model.addAttribute("sesiones", sesionesPage.getContent());
        model.addAttribute("sesionesPage", sesionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        model.addAttribute("rol", rol);
        model.addAttribute("roles", Rol.values());

        return "auditoria-sesiones";
    }

    /**
     * Auditoría de predicciones realizadas
     */
    @GetMapping("/auditoria/predicciones")
    public String mostrarAuditoriaPredicciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String rol,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Fechas por defecto: último mes
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "fechaPrediccion"));
        Page<AuditoriaPredicciones> prediccionesPage = prediccionesRepository
                .findByFechaPrediccionBetweenAndUsernameContainingIgnoreCaseAndRolContainingIgnoreCase(
                        start, end, username, rol, pageable);

        model.addAttribute("predicciones", prediccionesPage.getContent());
        model.addAttribute("prediccionesPage", prediccionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("username", username);
        model.addAttribute("rol", rol);
        model.addAttribute("roles", Rol.values());

        return "auditoria-predicciones";
    }
}