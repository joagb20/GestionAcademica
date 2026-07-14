package com.example.gestion_academica;

import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.repository.ProfesorRepository;
import com.example.gestion_academica.repository.UsuarioRepository;
import com.example.gestion_academica.service.GestorAcademicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProfesorRegistrationServiceTest {

    @Autowired
    private GestorAcademicoService gestorAcademicoService;

    @Autowired
    private ProfesorRepository profesorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void registrarProfesorDebeCrearUsuarioYProfesor() {
        Profesor profesor = gestorAcademicoService.registrarProfesor(
                "Ana Pérez",
                "secreta123",
                "Matemática"
        );

        assertThat(profesor.getId()).isNotNull();
        assertThat(profesor.getRol()).isEqualTo("ROLE_PROFESOR");
        assertThat(usuarioRepository.findByCorreo(profesor.getCorreo())).isNotNull();
        assertThat(profesorRepository.findById(profesor.getId())).isPresent();
    }
}
