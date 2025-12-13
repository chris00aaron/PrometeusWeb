package com.prometeus.prometeus.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.prometeus.prometeus.model.AuditoriaAccesos;
import com.prometeus.prometeus.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String showDashboard(Model model) {
        
        // 1. Obtener métricas de resumen
        Map<String, Object> summaryMetrics = dashboardService.getSummaryMetrics();
        model.addAllAttributes(summaryMetrics);

        // 2. Obtener usuarios más activos
        List<Object[]> topUsers = dashboardService.getTopActiveUsers();
        model.addAttribute("topUsuarios", topUsers);
        
        // 3. Obtener actividad de administración
        List<AuditoriaAccesos> recentCreations = dashboardService.getLastUserCreations();
        model.addAttribute("creacionesRecientes", recentCreations);

        // 4. Obtener Promedios de Parámetros de Entrada (NUEVA MÉTRICA DE BARRAS)
        model.addAttribute("inputParamsAvg", dashboardService.getAverageInputParameters());

        return "dashboard";
    }
}
