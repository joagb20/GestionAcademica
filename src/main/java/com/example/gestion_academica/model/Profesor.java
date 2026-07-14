package com.example.gestion_academica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "profesores")
@Getter
@Setter
@NoArgsConstructor
public class Profesor extends Usuario {

    private String clase;

    @OneToMany(mappedBy = "profesor")
    private List<Curso> cursosDictados;
}