package com.prometeus.prometeus.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.security.UsuarioDetallesSecurity;
import com.prometeus.prometeus.service.PrediccionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor

@Controller
public class HistoryController {

    private final PrediccionService prediccionService;

    @GetMapping("/history/old")
    public String showHistory(Model model, @AuthenticationPrincipal UsuarioDetallesSecurity userDetails) {
        Long userId = userDetails.getUsuarioId();

        // 2. Llama al servicio para obtener el historial
        List<Prediccion> userHistory = prediccionService.getHistoryForUser(userId);

        // 3. Añade el historial al modelo para que la vista lo use
        model.addAttribute("predictions", userHistory);

        // 4. Devuelve el nombre de la nueva vista (history.html)
        return "history";
    }

    @GetMapping("/history")
    public String showHistory(
            Model model,
            @AuthenticationPrincipal UsuarioDetallesSecurity userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        Long userId = userDetails.getUsuarioId();

        Page<Prediccion> predictionsPage = prediccionService.getHistoryFiltered(
                userId, page, startDate, endDate
        );

        model.addAttribute("predictionsPage", predictionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", predictionsPage.getTotalPages());

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "history";
    }
}
