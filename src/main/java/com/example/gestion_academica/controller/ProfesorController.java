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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profesor")
@RequiredArgsConstructor
public class ProfesorController {

    private final GestorAcademicoService gestorService;

    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ROLE_PROFESOR".equals(usuario.getRol())) {
            return "redirect:/login";
        }

        Profesor profesor = gestorService.obtenerProfesorPorCorreo(usuario.getCorreo());
        if (profesor == null) {
            return "redirect:/login";
        }

        List<Curso> cursos = gestorService.listarCursosPorProfesor(profesor.getId());
        List<Estudiante> estudiantes = gestorService.listarEstudiantesPorProfesor(profesor.getId());
        Map<Long, Double> promedios = estudiantes.stream()
                .collect(Collectors.toMap(Estudiante::getId, gestorService::calcularPromedio, (a, b) -> a, LinkedHashMap::new));

        model.addAttribute("profesor", profesor);
        model.addAttribute("cursos", cursos);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("promedios", promedios);
        return "prof-dashboard";
    }

    @GetMapping("/reportes")
    public String mostrarReportes(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ROLE_PROFESOR".equals(usuario.getRol())) {
            return "redirect:/login";
        }

        Profesor profesor = gestorService.obtenerProfesorPorCorreo(usuario.getCorreo());
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