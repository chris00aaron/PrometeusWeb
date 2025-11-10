package com.prometeus.prometeus;

import com.prometeus.prometeus.model.Rol;
import com.prometeus.prometeus.model.Usuario;
import com.prometeus.prometeus.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public TestDataLoader(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Revisa si el usuario de prueba ya existe
        if (usuarioRepository.findById(1L).isEmpty()) {
            System.out.println("Creando usuario de prueba...");
            Usuario testUser = Usuario.builder()
                    .username("testUser")
                    .clave("testPass") // En un futuro, esto debería estar encriptado
                    .rol(Rol.TRABAJADOR)
                    .ultimaConexion(LocalDateTime.now())
                    // @PrePersist se encargará de 'activo', 'fechaCreacion', etc.
                    .build();
            
            usuarioRepository.save(testUser);
            System.out.println("Usuario de prueba (ID 1) creado.");
        } else {
            System.out.println("Usuario de prueba (ID 1) ya existe.");
        }
    }
}