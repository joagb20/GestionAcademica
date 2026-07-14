package com.example.gestion_academica.config;

import com.example.gestion_academica.model.Administrador;
import com.example.gestion_academica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica si la tabla de usuarios está vacía
        if (usuarioRepository.count() == 0) {
            Administrador admin = new Administrador();
            admin.setNombre("Administrador Principal");
            
            // ESTAS SERÁN TUS CREDENCIALES DE INGRESO
            admin.setCorreo("admin@universidad.edu");
            admin.setPassword("admin123"); 
            
            admin.setRol("ROLE_ADMIN");
            admin.setCargo("Director General");

            usuarioRepository.save(admin);
            System.out.println("Usuario administrador creado por defecto.");
        }
    }
}