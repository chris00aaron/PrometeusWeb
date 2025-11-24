package com.prometeus.prometeus.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")            // vista GET
                .loginProcessingUrl("/login")    // POST del formulario
                .defaultSuccessUrl("/predict", true)  // después de login
                .failureUrl("/login?error=true")      // login incorrecto
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Con este metodo hacemos que la "Palabras claves de SECURITY" se posibles de interprepretar en el Thymeleaf
     * @return El inteprete para que reconosca las palabras claves
     */
    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    /**
     * Con esto damos a entender que trabajaremos con clientes de nuestras bd
     * Debido a este motivo no se generara ningun user por defaul
     * @return El Proviterario personalizados
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
