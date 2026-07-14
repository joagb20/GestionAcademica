package com.example.gestion_academica.controller;

import com.example.gestion_academica.model.Profesor;
import com.example.gestion_academica.service.GestorAcademicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final GestorAcademicoService gestorAcademicoService;

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("profesores", gestorAcademicoService.listarProfesores());
        model.addAttribute("cursos", gestorAcademicoService.listarCursos());
        model.addAttribute("estudiantes", gestorAcademicoService.listarEstudiantes());
        return "admin-dashboard";
    }

    @GetMapping("/registrar-profesor")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("profesor", new Profesor());
        return "admin-registrar-profesor";
    }

    @PostMapping("/registrar-profesor")
    public String registrarProfesor(@ModelAttribute("profesor") Profesor profesor, RedirectAttributes redirectAttributes) {
        Profesor registrado = gestorAcademicoService.registrarProfesor(
                profesor.getNombre(),
                profesor.getPassword(),
                profesor.getClase()
        );

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Profesor registrado correctamente. El correo de acceso generado es " + registrado.getCorreo()
        );

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/crear-curso")
    public String mostrarFormularioCurso(Model model) {
        model.addAttribute("profesores", gestorAcademicoService.listarProfesores());
        return "admin-crear-curso";
    }

    @PostMapping("/crear-curso")
    public String crearCurso(@RequestParam String nombre,
                             @RequestParam String codigo,
                             @RequestParam String descripcion,
                             @RequestParam String estructuraEvaluacion,
                             @RequestParam Long profesorId,
                             RedirectAttributes redirectAttributes) {
        gestorAcademicoService.crearCurso(nombre, codigo, descripcion, estructuraEvaluacion, profesorId);
        redirectAttributes.addFlashAttribute("mensajeExito", "Clase creada correctamente y lista para recibir estudiantes.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/asignar-estudiantes")
    public String mostrarAsignarEstudiantes(Model model) {
        model.addAttribute("cursos", gestorAcademicoService.listarCursos());
        model.addAttribute("estudiantes", gestorAcademicoService.listarEstudiantes());
        return "admin-asignar-estudiantes";
    }

    @PostMapping("/asignar-estudiantes")
    public String asignarEstudiantes(@RequestParam Long cursoId,
                                     @RequestParam Long estudianteId,
                                     RedirectAttributes redirectAttributes) {
        gestorAcademicoService.asignarEstudianteACurso(cursoId, estudianteId);
        redirectAttributes.addFlashAttribute("mensajeExito", "El estudiante fue añadido a la clase del docente correctamente.");
        return "redirect:/admin/dashboard";
    }
}