package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/nombre")
    public String obtenerNombre(@RequestParam String email) {
        // Buscamos al usuario en la base de datos
        Usuario user = usuarioRepository.findByEmail(email);
        
        // Si existe, devolvemos su nombre real. Si no, un genérico.
        if (user != null) {
            return user.getNombre();
        } else {
            return "Usuario";
        }
    }
}