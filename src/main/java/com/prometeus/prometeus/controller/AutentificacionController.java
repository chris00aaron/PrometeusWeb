package com.prometeus.prometeus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AutentificacionController {
    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }
}