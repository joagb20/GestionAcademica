package com.example.gestion_academica.controller;

import com.example.gestion_academica.model.Curso;
import com.example.gestion_academica.model.Estudiante;
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

    // --- NUEVAS RUTAS: CREAR ALUMNO ---
    @GetMapping("/estudiantes/nuevo")
    public String mostrarFormularioCrearEstudiante(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        return "admin-crear-estudiante";
    }

    @PostMapping("/estudiantes")
    public String guardarEstudiante(@ModelAttribute Estudiante estudiante, RedirectAttributes redirectAttrs) {
        // Guardamos al estudiante a través del servicio
        gestorAcademicoService.registrarEstudiante(estudiante);
        redirectAttrs.addFlashAttribute("mensajeExito", "Alumno registrado en el sistema con éxito.");
        return "redirect:/admin/dashboard";
    }

    // --- RUTAS MODIFICADAS: CREAR CURSO CON PORCENTAJES ---
    @GetMapping("/crear-curso")
    public String mostrarFormularioCurso(Model model) {
        model.addAttribute("profesores", gestorAcademicoService.listarProfesores());
        model.addAttribute("curso", new Curso());
        return "admin-crear-curso";
    }

    @PostMapping("/crear-curso")
    public String crearCurso(@RequestParam String nombre,
                             @RequestParam String codigo,
                             @RequestParam(required = false) String descripcion,
                             @RequestParam Double porcentajePc1,
                             @RequestParam Double porcentajePc2,
                             @RequestParam Double porcentajeParcial,
                             @RequestParam Double porcentajeFinal,
                             @RequestParam Long profesorId,
                             RedirectAttributes redirectAttributes) {
                             
        gestorAcademicoService.crearCursoConPorcentajes(nombre, codigo, descripcion, porcentajePc1, porcentajePc2, porcentajeParcial, porcentajeFinal, profesorId);
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