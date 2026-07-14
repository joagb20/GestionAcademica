package com.example.gestion_academica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    private String cargo;
    
 } 