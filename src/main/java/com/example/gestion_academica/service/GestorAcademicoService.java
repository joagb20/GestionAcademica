package com.example.gestion_academica.service;

import com.example.gestion_academica.model.Curso;
import com.example.gestion_academica.model.Estudiante;
import com.example.gestion_academica.model.Nota;
import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.model.Usuario;
import com.example.gestion_academica.repository.CursoRepository;
import com.example.gestion_academica.repository.EstudianteRepository;
import com.example.gestion_academica.repository.ProfesorRepository;
import com.example.gestion_academica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestorAcademicoService {

    private final EstudianteRepository estudianteRepository;
    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    /**
     * USO DE: map() y reduce()[cite: 1]
     * MANEJO DE EXCEPCIONES: try-catch[cite: 1]
     * Función: Calcular promedios[cite: 1]
     */
    public Double calcularPromedio(Estudiante estudiante) {
        try {
            List<Nota> notas = estudiante.getNotas();
            if (notas == null || notas.isEmpty()) {
                return 0.0;
            }

            Double suma = notas.stream()
                    .map(Nota::getValor)
                    .reduce(0.0, Double::sum);

            return suma / notas.size();

        } catch (Exception e) {
            System.err.println("Error al calcular el promedio: " + e.getMessage());
            return 0.0;
        }
    }

    public List<Estudiante> filtrarEstudiantesEnRiesgo() {
        List<Estudiante> todos = estudianteRepository.findAll();

        return todos.stream()
                .filter(estudiante -> calcularPromedio(estudiante) < 10.5)
                .collect(Collectors.toList());
    }

    public Map<String, String> generarReporteEstadosAcademicos() {
        List<Estudiante> estudiantes = estudianteRepository.findAll();

        Map<String, String> reporte = new HashMap<>();

        estudiantes.forEach(estudiante -> {
            double promedio = calcularPromedio(estudiante);
            String estado;

            if (promedio >= 12.0) {
                estado = "APROBADO";
            } else {
                estado = "DESAPROBADO";
            }

            reporte.put(estudiante.getCodigoMatricula(), estado);
        });

        return reporte;
    }

    @Transactional
    public Profesor registrarProfesor(String nombre, String password, String clase) {
        String correo = generarCorreoUnico(nombre);

        Profesor profesor = new Profesor();
        profesor.setNombre(nombre);
        profesor.setCorreo(correo);
        profesor.setPassword(password);
        profesor.setRol("ROLE_PROFESOR");
        profesor.setClase(clase);

        return profesorRepository.save(profesor);
    }

    @Transactional
    public Curso crearCurso(String nombre, String codigo, String descripcion, String estructuraEvaluacion, Long profesorId) {
        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado"));

        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setCodigo(codigo);
        curso.setDescripcion(descripcion);
        curso.setEstructuraEvaluacion(estructuraEvaluacion);
        curso.setProfesor(profesor);
        return cursoRepository.save(curso);
    }

    @Transactional
    public void asignarEstudianteACurso(Long cursoId, Long estudianteId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        if (curso.getEstudiantes() == null) {
            curso.setEstudiantes(new ArrayList<>());
        }
        if (!curso.getEstudiantes().contains(estudiante)) {
            curso.getEstudiantes().add(estudiante);
        }

        if (estudiante.getCursosMatriculados() == null) {
            estudiante.setCursosMatriculados(new ArrayList<>());
        }
        if (!estudiante.getCursosMatriculados().contains(curso)) {
            estudiante.getCursosMatriculados().add(curso);
        }

        cursoRepository.save(curso);
    }

    public List<Profesor> listarProfesores() {
        return profesorRepository.findAll();
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteRepository.findAll();
    }

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public List<Curso> listarCursosPorProfesor(Long profesorId) {
        return cursoRepository.findByProfesorId(profesorId);
    }

    public List<Estudiante> listarEstudiantesPorCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        return curso.getEstudiantes() == null ? List.of() : curso.getEstudiantes();
    }

    public List<Estudiante> listarEstudiantesPorProfesor(Long profesorId) {
        return listarCursosPorProfesor(profesorId).stream()
                .flatMap(curso -> listarEstudiantesPorCurso(curso.getId()).stream())
                .distinct()
                .toList();
    }

    public Profesor obtenerProfesorPorCorreo(String correo) {
        return profesorRepository.findByCorreo(correo).orElse(null);
    }

    private String generarCorreoUnico(String nombre) {
        String base = nombre == null ? "profesor" : nombre.trim().toLowerCase().replaceAll("\\s+", ".");
        if (base.isBlank()) {
            base = "profesor";
        }

        String correo = base + "@universidad.edu";
        int contador = 1;
        while (usuarioRepository.findByCorreo(correo) != null) {
            correo = base + contador + "@universidad.edu";
            contador++;
        }
        return correo;
    }

    public Usuario autenticarUsuario(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            return null;
        }

        if (password.equals(usuario.getPassword())) {
            return usuario;
        }

        return null;
    }
}