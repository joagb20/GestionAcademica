package com.example.gestion_academica.config;

import com.example.gestion_academica.model.Administrador;
import com.example.gestion_academica.model.Estudiante;
import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.model.Curso;
import com.example.gestion_academica.repository.UsuarioRepository;
import com.example.gestion_academica.repository.EstudianteRepository;
import com.example.gestion_academica.repository.ProfesorRepository;
import com.example.gestion_academica.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

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

            // Crear profesores de prueba
            Profesor prof1 = new Profesor();
            prof1.setNombre("Juan Martínez");
            prof1.setCorreo("juan.martinez@universidad.edu");
            prof1.setPassword("prof123");
            prof1.setRol("ROLE_PROFESOR");
            prof1.setClase("Matemática");
            profesorRepository.save(prof1);

            Profesor prof2 = new Profesor();
            prof2.setNombre("María García");
            prof2.setCorreo("maria.garcia@universidad.edu");
            prof2.setPassword("prof123");
            prof2.setRol("ROLE_PROFESOR");
            prof2.setClase("Historia");
            profesorRepository.save(prof2);

            // Crear estudiantes de prueba
            Estudiante est1 = new Estudiante();
            est1.setNombre("Carlos López");
            est1.setCorreo("carlos.lopez@universidad.edu");
            est1.setPassword("est123");
            est1.setRol("ROLE_ESTUDIANTE");
            est1.setCodigoMatricula("EST-001");
            estudianteRepository.save(est1);

            Estudiante est2 = new Estudiante();
            est2.setNombre("Ana Rodríguez");
            est2.setCorreo("ana.rodriguez@universidad.edu");
            est2.setPassword("est123");
            est2.setRol("ROLE_ESTUDIANTE");
            est2.setCodigoMatricula("EST-002");
            estudianteRepository.save(est2);

            // Crear cursos
            Curso curso1 = new Curso();
            curso1.setNombre("Cálculo I");
            curso1.setCodigo("CALC-101");
            curso1.setDescripcion("Introducción al cálculo diferencial");
            curso1.setEstructuraEvaluacion("Participación 20%, Parcial 30%, Final 50%");
            curso1.setProfesor(prof1);
            cursoRepository.save(curso1);

            Curso curso2 = new Curso();
            curso2.setNombre("Historia Universal");
            curso2.setCodigo("HIST-101");
            curso2.setDescripcion("Historia desde la antigüedad hasta el presente");
            curso2.setEstructuraEvaluacion("Tareas 25%, Examen 75%");
            curso2.setProfesor(prof2);
            cursoRepository.save(curso2);

            // Asignar estudiantes a cursos
            curso1.getEstudiantes().add(est1);
            curso1.getEstudiantes().add(est2);
            curso2.getEstudiantes().add(est1);
            cursoRepository.save(curso1);
            cursoRepository.save(curso2);

            est1.getCursosMatriculados().add(curso1);
            est1.getCursosMatriculados().add(curso2);
            est2.getCursosMatriculados().add(curso1);
            estudianteRepository.save(est1);
            estudianteRepository.save(est2);

            System.out.println("Datos de prueba creados correctamente.");
        }
    }
}