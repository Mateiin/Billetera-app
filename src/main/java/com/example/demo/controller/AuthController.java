package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Clase auxiliar para recibir los datos del JSON
    static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // 1. Imprimimos en la terminal qué llegó desde la web
        System.out.println("------------------------------------------------");
        System.out.println("📨 SOLICITUD DE LOGIN RECIBIDA:");
        System.out.println("   -> Email: '" + request.email + "'");
        System.out.println("   -> Pass:  '" + request.password + "'");

        // 2. Buscamos en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(request.email);

        if (usuario == null) {
            System.out.println("❌ ERROR: No existe ningún usuario con ese email en la BD.");
            return "LOGIN_FALLIDO";
        }

        // 3. Verificamos la contraseña
        System.out.println("   -> Usuario encontrado en BD (ID: " + usuario.getId() + ")");
        
        if (usuario.getPassword().equals(request.password)) {
            System.out.println("🎉 ¡EXITO! Contraseña correcta. Acceso concedido.");
            return "LOGIN_EXITOSO";
        } else {
            System.out.println("⛔ ERROR: La contraseña no coincide con la guardada.");
            return "LOGIN_FALLIDO";
        }
    }
}