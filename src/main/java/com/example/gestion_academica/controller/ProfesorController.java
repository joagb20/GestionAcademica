package com.example.gestion_academica.controller;

import com.example.gestion_academica.model.Curso;
import com.example.gestion_academica.model.Estudiante;
import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.model.Usuario;
import com.example.gestion_academica.service.GestorAcademicoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profesor")
@RequiredArgsConstructor
public class ProfesorController {

    private final GestorAcademicoService gestorService;

    private Profesor obtenerProfesorDelSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ROLE_PROFESOR".equals(usuario.getRol())) {
            return null;
        }
        return gestorService.obtenerProfesorPorCorreo(usuario.getCorreo());
    }

    private String obtenerApellido(Estudiante estudiante) {
        if (estudiante == null || estudiante.getNombre() == null) {
            return "";
        }
        String[] partes = estudiante.getNombre().trim().split("\\s+");
        return partes.length == 0 ? "" : partes[partes.length - 1].toLowerCase();
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) {
            return "redirect:/login";
        }

        List<Curso> cursos = gestorService.listarCursosPorProfesor(profesor.getId());
        cursos.forEach(curso -> curso.setEstudiantes(curso.getEstudiantes().stream()
                .sorted(Comparator.comparing(this::obtenerApellido).thenComparing(Estudiante::getNombre))
                .toList()));

        Map<Long, Map<Long, Map<String, Double>>> notasPorCursoEstudiante = new LinkedHashMap<>();
        for (Curso curso : cursos) {
            Map<Long, Map<String, Double>> estudiantesNotas = new LinkedHashMap<>();
            gestorService.listarNotasPorCurso(curso.getId()).forEach(nota -> {
                estudiantesNotas.computeIfAbsent(nota.getEstudiante().getId(), id -> new LinkedHashMap<>())
                        .put(nota.getTipoEvaluacion(), nota.getValor());
            });
            notasPorCursoEstudiante.put(curso.getId(), estudiantesNotas);
        }

        model.addAttribute("profesor", profesor);
        model.addAttribute("cursos", cursos);
        model.addAttribute("notasPorCursoEstudiante", notasPorCursoEstudiante);
        return "prof-dashboard";
    }

    @GetMapping("/registrar-notas")
    public String mostrarRegistroNotas(HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) {
            return "redirect:/login";
        }

        List<Curso> cursos = gestorService.listarCursosPorProfesor(profesor.getId());
        List<Estudiante> estudiantes = gestorService.listarEstudiantesPorProfesor(profesor.getId());

        model.addAttribute("profesor", profesor);
        model.addAttribute("cursos", cursos);
        model.addAttribute("estudiantes", estudiantes);
        return "prof-registrar-notas";
    }

    @PostMapping("/registrar-nota")
    public String registrarNota(HttpSession session,
                               @RequestParam Long cursoId,
                               @RequestParam String tipoEvaluacion,
                               @RequestParam(required = false) Double valor,
                               @RequestParam(name = "ns", defaultValue = "false") boolean ns,
                               RedirectAttributes redirectAttributes) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) {
            return "redirect:/login";
        }

        if (ns) {
            valor = -1.0;
        } else if (valor == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debes ingresar una calificación o marcar Ns.");
            return "redirect:/profesor/registrar-notas";
        }

        gestorService.registrarNotasParaCurso(cursoId, valor, tipoEvaluacion);
        redirectAttributes.addFlashAttribute("mensajeExito", "Notas registradas correctamente para todos los estudiantes del curso.");
        return "redirect:/profesor/registrar-notas";
    }

    @GetMapping("/reportes")
    public String mostrarReportes(HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) {
            return "redirect:/login";
        }

        List<Estudiante> estudiantes = gestorService.listarEstudiantesPorProfesor(profesor.getId());
        Map<String, String> reporte = new LinkedHashMap<>();
        estudiantes.forEach(estudiante -> {
            double promedio = gestorService.calcularPromedio(estudiante);
            String estado = promedio >= 12.0 ? "APROBADO" : "DESAPROBADO";
            reporte.put(estudiante.getCodigoMatricula(), estado);
        });

        model.addAttribute("profesor", profesor);
        model.addAttribute("reporte", reporte);
        return "prof-reportes";
    }
}