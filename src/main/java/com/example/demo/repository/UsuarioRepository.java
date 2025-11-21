package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // ¡Aquí no hay código! Spring Data JPA escribe el SQL por nosotros.
    // Al extender de JpaRepository, ya ganamos métodos como:
    // .save(usuario)   -> Guardar
    // .findAll()       -> Buscar todos
    // .findById(id)    -> Buscar uno
    // .delete(usuario) -> Borrar
    
    // Si quisiéramos buscar por email, solo declaramos el método así:
    Usuario findByEmail(String email);
}