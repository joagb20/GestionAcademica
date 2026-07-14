package com.example.gestion_academica;

import com.example.gestion_academica.model.Curso;
import com.example.gestion_academica.model.Estudiante;
import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.repository.CursoRepository;
import com.example.gestion_academica.repository.EstudianteRepository;
import com.example.gestion_academica.service.GestorAcademicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GestorAcademicoServiceManagementTest {

    @Autowired
    private GestorAcademicoService gestorAcademicoService;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Test
    void crearCursoYAsignarEstudianteDebePersistirRelaciones() {
        Profesor profesor = gestorAcademicoService.registrarProfesor("Laura Castro", "pass123", "Historia");

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre("Carlos Méndez");
        estudiante.setCorreo("carlos@universidad.edu");
        estudiante.setPassword("secret");
        estudiante.setRol("ROLE_ESTUDIANTE");
        estudiante.setCodigoMatricula("MAT-1001");
        estudiante = estudianteRepository.save(estudiante);

        Curso curso = gestorAcademicoService.crearCurso(
                "Historia Universal",
                "HIU-101",
                "Semestre 2026-1",
                "Participación 20%, Parcial 30%, Final 50%",
                profesor.getId()
        );

        gestorAcademicoService.asignarEstudianteACurso(curso.getId(), estudiante.getId());

        Curso persistido = cursoRepository.findById(curso.getId()).orElseThrow();

        assertThat(persistido.getProfesor().getId()).isEqualTo(profesor.getId());
        assertThat(persistido.getEstudiantes()).extracting(Estudiante::getId).contains(estudiante.getId());
        assertThat(gestorAcademicoService.listarEstudiantesPorCurso(curso.getId()))
                .extracting(Estudiante::getId)
                .contains(estudiante.getId());
    }
}
