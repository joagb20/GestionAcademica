package com.example.gestion_academica.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true)
    private String codigo;

    private Integer creditos;

    @Column(length = 1000)
    private String descripcion;

    // Se mantiene por compatibilidad con tus Tests y DataInitializer
    @Column(length = 1000)
    private String estructuraEvaluacion;

    // Nuevos campos: Porcentajes fijos para la evaluación
    private Double porcentajePc1;
    private Double porcentajePc2;
    private Double porcentajeParcial;
    private Double porcentajeFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToMany
    @JoinTable(
            name = "curso_estudiantes",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Estudiante> estudiantes = new ArrayList<>();
}