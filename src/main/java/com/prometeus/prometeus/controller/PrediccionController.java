package com.prometeus.prometeus.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.model.dto.PrediccionRequest;
import com.prometeus.prometeus.service.PrediccionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor

@Controller
public class PrediccionController {
    private final PrediccionService prediccionService;
    
    @GetMapping("/predict")
    public String showPredictionForm(@ModelAttribute("predictionRequest") PrediccionRequest predictionRequest,Model model) {
        // Usamos el DTO como objeto del formulario
        model.addAttribute("predictionResult", null);
        // Asume que tu HTML se llama "prediction-engine.html"
        return "prediction-engine";
    }

    @PostMapping("/predict")
    public String handlePredictionSubmit(@Valid @ModelAttribute PrediccionRequest predictionRequest, 
            BindingResult validation, RedirectAttributes redireccion, Model model, HttpSession sesion) {

        Long userId = ((Usuario) sesion.getAttribute("user")).getId();

        // 1. Validar el formulario
        if (validation.hasErrors()) {
            List<String> errores = new ArrayList<>();
            //Mapeamos los errores
            validation.getFieldErrors().forEach(error -> errores.add(error.getDefaultMessage()));
            redireccion.addFlashAttribute("erroresValidacion", errores);

            //Reenviamos el formulario
            redireccion.addFlashAttribute("predictionRequest", predictionRequest);
            return "redirect:/predict"; // Volver al formulario con errores
        }

        try {

            // 2. Llama al servicio, que ahora hace todo (API + Guardar)
            BigDecimal result = prediccionService.getPredictionAndSave(predictionRequest, userId);
            // 3. Devuelve los datos a la vista
            model.addAttribute("predictionRequest", predictionRequest); // Mantiene los datos en el formulario
            model.addAttribute("predictionResult", result); // Muestra el resultado

        } catch (RuntimeException e) {
            // Manejo de errores
            model.addAttribute("erroresValidacion", List.of("Error al obtener la predicción: " + e.getMessage()));
            model.addAttribute("predictionResult", null);
            model.addAttribute("predictionRequest", predictionRequest);
        } 
        return "prediction-engine";
    }

    @GetMapping("/history")
    public String showHistory(Model model, HttpSession sesion) {
        Long userId = ((Usuario) sesion.getAttribute("user")).getId();

        // 2. Llama al servicio para obtener el historial
        List<Prediccion> userHistory = prediccionService.getHistoryForUser(userId);

        // 3. Añade el historial al modelo para que la vista lo use
        model.addAttribute("predictions", userHistory);

        // 4. Devuelve el nombre de la nueva vista (history.html)
        return "history";
    }
}
