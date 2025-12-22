package com.prometeus.prometeus.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.prometeus.prometeus.dto.PrediccionRequest;

import com.prometeus.prometeus.security.UsuarioDetallesSecurity;
import com.prometeus.prometeus.service.CSVService;
import com.prometeus.prometeus.service.PrediccionService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j

@Controller
@RequestMapping("/predict")
public class PrediccionController {
    private final PrediccionService prediccionService;
    private final CSVService csvService;

    /*
     * *********************************
     * Para procesar solo una prediccion*
     ************************************/
    @GetMapping()
    public String showPredictionForm(@ModelAttribute("predictionRequest") PrediccionRequest predictionRequest,
            Model model) {
        // Usamos el DTO como objeto del formulario
        model.addAttribute("predictionResult", null);
        // Asume que tu HTML se llama "prediction-engine.html"
        return "prediction-engine";
    }

    @PostMapping()
    public String handlePredictionSubmit(@Valid @ModelAttribute PrediccionRequest predictionRequest,
            BindingResult validation, RedirectAttributes redireccion, Model model,
            @AuthenticationPrincipal UsuarioDetallesSecurity userDetails) {

        Long userId = userDetails.getUsuarioId();

        // 1. Validar el formulario
        if (validation.hasErrors()) {
            List<String> errores = new ArrayList<>();
            // Mapeamos los errores
            validation.getFieldErrors().forEach(error -> errores.add(error.getDefaultMessage()));
            redireccion.addFlashAttribute("erroresValidacion", errores);

            // Reenviamos el formulario
            redireccion.addFlashAttribute("predictionRequest", predictionRequest);
            return "redirect:/predict"; // Volver al formulario con errores
        }

        // 2. Llama al servicio, que ahora hace todo (API + Guardar)
        BigDecimal result = prediccionService.getPredictionAndSave(predictionRequest, userId);

        // 3. Devuelve los datos a la vista
        model.addAttribute("predictionRequest", predictionRequest); // Mantiene los datos en el formulario
        model.addAttribute("predictionResult", result); // Muestra el resultado

        return "prediction-engine";
    }

    /*
     * ************************************
     * Para procesar multiples predicciones*
     ***************************************/
    @GetMapping("/cargar")
    public String obtenerCargardo() {
        return "prediction-multi";
    }

    @PostMapping("/multiple")
    public ResponseEntity<byte[]> uploadCsv(
            @RequestParam("archivo") MultipartFile file,
            @AuthenticationPrincipal UsuarioDetallesSecurity userDetails) {

        try {
            // Leer CSV
            List<PrediccionRequest> datos = csvService.parseCsv(file);

            // Validar datos
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            List<String> errores = new ArrayList<>();

            for (int i = 0; i < datos.size(); i++) {
                for (ConstraintViolation<PrediccionRequest> v : validator.validate(datos.get(i))) {
                    errores.add("Línea " + (i + 2) + " → " + v.getMessage());
                }
            }

            if (!errores.isEmpty()) {
                // Si hay errores → devolver lista de errores como texto descargable
                String contenido = String.join("\n", errores);
                return ResponseEntity.badRequest()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=errores.csv")
                        .body(contenido.getBytes(StandardCharsets.UTF_8));
            }

            // Obtener predicciones
            Long userId = userDetails.getUsuarioId();
            List<BigDecimal> resultados = new ArrayList<>();
            for (PrediccionRequest d : datos) {
                resultados.add(prediccionService.getPredictionAndSave(d, userId));
            }

            // Generar CSV final
            byte[] archivoFinal = csvService.exportarResultados(datos, resultados);

            // Retornar archivo descargable
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=predicciones.csv")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(archivoFinal);

        } catch (Exception e) {
            String error = "Error procesando archivo: " + e.getMessage();
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=error.csv")
                    .body(error.getBytes(StandardCharsets.UTF_8));
        }
    }
}
