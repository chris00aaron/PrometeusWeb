package com.prometeus.prometeus.controlador;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.prometeus.prometeus.dto.PrediccionRequest;
import com.prometeus.prometeus.model.Prediccion;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.service.PrediccionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PrediccionController {
    private final PrediccionService prediccionService;

    public PrediccionController(PrediccionService prediccionService) {
        this.prediccionService = prediccionService;
    }

    @GetMapping("/predict")
    public String showPredictionForm(Model model) {
        // Usamos el DTO como objeto del formulario
        model.addAttribute("predictionRequest", new PrediccionRequest());
        model.addAttribute("predictionResult", null);
        // Asume que tu HTML se llama "prediction-engine.html"
        return "prediction-engine";
    }

    @PostMapping("/predict")
    public String handlePredictionSubmit(@ModelAttribute PrediccionRequest predictionRequest, Model model, HttpSession sesion) {
        Long userId = ((Usuario) sesion.getAttribute("user")).getId();
        
        // 1. Llama al servicio, que ahora hace todo (API + Guardar)
        BigDecimal result = prediccionService.getPredictionAndSave(predictionRequest, userId);
        
        // 2. Devuelve los datos a la vista
        model.addAttribute("predictionRequest", predictionRequest); // Mantiene los datos en el formulario
        model.addAttribute("predictionResult", result);             // Muestra el resultado
        
        return "prediction-engine";
    }

    @GetMapping("/")
    public String showIndex() {
        return "index"; // Devuelve index.html
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
