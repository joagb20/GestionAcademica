package com.example.gestion_academica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.gestion_academica.model.Estudiante;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    // Spring Data JPA automáticamente implementa esta consulta
    Estudiante findByCodigoMatricula(String codigoMatricula);
}
