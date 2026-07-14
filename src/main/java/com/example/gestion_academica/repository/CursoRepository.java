package com.example.gestion_academica.repository;

import com.example.gestion_academica.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByProfesorId(Long profesorId);
}
