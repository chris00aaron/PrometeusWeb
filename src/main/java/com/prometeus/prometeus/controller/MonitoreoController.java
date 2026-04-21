package com.prometeus.prometeus.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.prometeus.prometeus.dto.MonitoreoDTO;
import com.prometeus.prometeus.model.Motor;
import com.prometeus.prometeus.service.MonitoreoService;
import com.prometeus.prometeus.service.MotorService;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@Controller
@RequestMapping("/monitoreo")
public class MonitoreoController {

    private final MonitoreoService monitoreoService;
    private final MotorService motorService;

    @GetMapping
    public String mostrarMonitoreo(Model model) {
        // Obtener motores activos
        List<Motor> motoresActivos = motorService.obtenerMotoresActivos();

        if (!motoresActivos.isEmpty()) {
            Motor primerMotor = motoresActivos.get(0);
            List<MonitoreoDTO> datosMonitoreo = monitoreoService.obtenerMonitoreosUltimosMinutos(primerMotor.getId(), 1200)
                    .stream()
                    .map(monitoreo -> new MonitoreoDTO(monitoreo.getFechaIngreso(), monitoreo.getTemperatura().toString()))
                    .toList(); // Últimos 60 minutos

            datosMonitoreo.forEach(System.out::println);
            model.addAttribute("monitoreos", datosMonitoreo);
        }
        model.addAttribute("motores", motoresActivos);
        return "monitoreo"; // La vista "monitoreo.jsp" o "monitoreo.html" en el directorio de vistas
    }

    @GetMapping("/actualizarMonitoreo")
    @ResponseBody
    public List<MonitoreoDTO> actualizarMonitoreo(@RequestParam Long motorId, @RequestParam int minutos) {
        System.out.println("motorId: " + motorId + " minutos: " + minutos);

        List<MonitoreoDTO> datosMonitoreo = monitoreoService.obtenerMonitoreosUltimosMinutos(motorId, minutos)
                .stream()
                .map(monitoreo -> new MonitoreoDTO(monitoreo.getFechaIngreso(), monitoreo.getTemperatura().toString()))
                .toList();

        datosMonitoreo.forEach(System.out::println);
        return datosMonitoreo;
    }
}
