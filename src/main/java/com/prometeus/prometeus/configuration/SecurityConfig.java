package com.prometeus.prometeus.configuration; // O tu paquete principal

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer; // <-- ¡Asegúrate de importar esto!
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. Permite tus rutas públicas (index y predict)
                .requestMatchers("/", "/predict", "/history").permitAll() 
                
                // 2. Permite recursos estáticos (CSS, JS, etc.)
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // 3. Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            // 4. LA CORRECCIÓN: Habilita el formulario de login por defecto
            .formLogin(Customizer.withDefaults())

            // (Esta línea de httpBasic ya no es necesaria si usas formLogin)
            
            // 5. Mantenemos CSRF deshabilitado para que tu POST funcione
            .csrf(csrf -> csrf.disable()); 

        return http.build();
    }
}