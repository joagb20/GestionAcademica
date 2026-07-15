package com.example.gestion_academica.repository;

import com.example.gestion_academica.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByEstudianteId(Long estudianteId);
    List<Nota> findByCursoId(Long cursoId);
    Optional<Nota> findFirstByEstudianteIdAndCursoIdAndTipoEvaluacion(Long estudianteId, Long cursoId, String tipoEvaluacion);
}