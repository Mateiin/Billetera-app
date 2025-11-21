package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios") // Esto crea la tabla 'usuarios' en la base de datos
@Data // Lombok crea automáticamente los Getters, Setters y toString
@NoArgsConstructor // Crea el constructor vacío necesario para JPA
@AllArgsConstructor // Crea un constructor con todos los argumentos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true) // El email no se puede repetir
    private String email;

    private String password;
    
    // Aquí podríamos agregar más datos como 'telefono' o 'fechaNacimiento'
}