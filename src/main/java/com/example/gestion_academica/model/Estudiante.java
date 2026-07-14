package com.example.gestion_academica.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
public class Estudiante extends Usuario {

    @Column(unique = true, nullable = false)
    private String codigoMatricula;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<Nota> notas;

    @ManyToMany(mappedBy = "estudiantes")
    private List<Curso> cursosMatriculados = new ArrayList<>();
}