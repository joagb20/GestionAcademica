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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profesor")
@RequiredArgsConstructor
public class ProfesorController {

    private final GestorAcademicoService gestorService;

    // Método auxiliar para obtener el profesor de la sesión actual
    private Profesor obtenerProfesorDelSesion(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"ROLE_PROFESOR".equals(usuario.getRol())) {
            return null;
        }
        return gestorService.obtenerProfesorPorCorreo(usuario.getCorreo());
    }

    // 1. DASHBOARD PRINCIPAL: Solo muestra el resumen del profesor y sus clases
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) {
            return "redirect:/login";
        }

        // Solo traemos los cursos asignados al profesor
        List<Curso> cursos = gestorService.listarCursosPorProfesor(profesor.getId());

        model.addAttribute("profesor", profesor);
        model.addAttribute("cursos", cursos);
        
        return "prof-dashboard"; // Muestra la vista del panel principal
    }

    // 2. VISTA DE CLASE (NOTAS): Se abre al hacer clic en un curso específico desde el dashboard
    @GetMapping("/clase/{idCurso}")
    public String verClaseYNotas(@PathVariable Long idCurso, HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) return "redirect:/login";

        // Buscamos el curso específico y la lista de sus estudiantes
        Curso curso = gestorService.obtenerCursoPorId(idCurso);
        List<Estudiante> estudiantes = curso.getEstudiantes();

        model.addAttribute("profesor", profesor);
        model.addAttribute("curso", curso);
        model.addAttribute("estudiantes", estudiantes);

        return "prof-registrar-notas"; // Muestra la vista tipo "lista de asistencia"
    }

    // 3. GUARDAR NOTAS: Procesa el formulario de la vista de la clase
    @PostMapping("/guardarNota")
    public String guardarNotaEstudiante(
            HttpSession session,
            @RequestParam Long cursoId,
            @RequestParam Long estudianteId,
            @RequestParam(required = false) Double notaPc1,
            @RequestParam(required = false) Double notaPc2,
            @RequestParam(required = false) Double notaParcial,
            @RequestParam(required = false) Double notaFinal,
            RedirectAttributes redirectAttrs) {

        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) return "redirect:/login";

        // Registramos solo las notas que el profesor haya ingresado en el formulario
        if(notaPc1 != null) gestorService.registrarNota(estudianteId, cursoId, notaPc1, "PC1");
        if(notaPc2 != null) gestorService.registrarNota(estudianteId, cursoId, notaPc2, "PC2");
        if(notaParcial != null) gestorService.registrarNota(estudianteId, cursoId, notaParcial, "Parcial");
        if(notaFinal != null) gestorService.registrarNota(estudianteId, cursoId, notaFinal, "Final");

        redirectAttrs.addFlashAttribute("mensajeExito", "Notas guardadas correctamente para el alumno.");
        
        // Redirige de vuelta a la vista de esa misma clase para seguir calificando
        return "redirect:/profesor/clase/" + cursoId;
    }

    // Redirección de seguridad por si intentan entrar a una URL antigua
    @GetMapping("/registrar-notas")
    public String mostrarRegistroNotas(HttpSession session, Model model) {
        Profesor profesor = obtenerProfesorDelSesion(session);
        if (profesor == null) return "redirect:/login";
        
        return "redirect:/profesor/dashboard"; // Los devuelve al dashboard para que elijan su clase
    }

    // 4. REPORTES: Muestra el estado general de los alumnos
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