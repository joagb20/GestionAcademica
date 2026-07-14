package com.example.gestion_academica.controller;

import com.example.gestion_academica.model.Administrador;
import com.example.gestion_academica.model.Usuario;
import com.example.gestion_academica.service.GestorAcademicoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final GestorAcademicoService gestorAcademicoService;

    @GetMapping("/")
    public String redireccionarRaiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(String correo, String password, HttpSession session) {
        if ("admin@universidad.edu".equals(correo) && "admin123".equals(password)) {
            Administrador administrador = new Administrador();
            administrador.setNombre("Administrador");
            administrador.setCorreo(correo);
            administrador.setPassword(password);
            administrador.setRol("ROLE_ADMIN");
            administrador.setCargo("Administrador");
            session.setAttribute("usuario", administrador);
            return "redirect:/admin/dashboard";
        }

        Usuario usuario = gestorAcademicoService.autenticarUsuario(correo, password);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            if ("ROLE_PROFESOR".equals(usuario.getRol())) {
                return "redirect:/profesor/dashboard";
            }
            if ("ROLE_ESTUDIANTE".equals(usuario.getRol())) {
                return "redirect:/profesor/dashboard";
            }
        }

        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}