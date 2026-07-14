package com.example.gestion_academica.repository;

import com.example.gestion_academica.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Este método nos servirá más adelante si quieres hacer 
    // una validación de login real buscando el usuario en la base de datos
    Usuario findByCorreo(String correo);
}