package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    static class LoginRequest { public String email; public String password; }
    static class RegisterRequest { public String nombre; public String email; public String password; }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email);
        if (usuario == null) return "LOGIN_FALLIDO";
        if (usuario.getPassword().equals(request.password)) return "LOGIN_EXITOSO";
        return "LOGIN_FALLIDO";
    }

    @PostMapping("/register")
    public String registrar(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email) != null) return "ERROR_EMAIL_DUPLICADO";

        Usuario user = new Usuario();
        user.setNombre(request.nombre);
        user.setEmail(request.email);
        user.setPassword(request.password);
        usuarioRepository.save(user);

        // 1. CREAR CUENTA EN PESOS (ARS)
        crearCuenta(user, "ARS");

        // 2. CREAR CUENTA EN DÓLARES (USD)
        crearCuenta(user, "USD");

        return "REGISTRO_EXITOSO";
    }

    // --- MÉTODO AUXILIAR PARA NO REPETIR CÓDIGO ---
    private void crearCuenta(Usuario user, String moneda) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Caja de Ahorro " + moneda);
        cuenta.setMoneda(moneda);
        cuenta.setSaldo(new BigDecimal("0.00"));
        cuenta.setUsuario(user);
        cuenta.setCbu(generarCBU());

        // Alias diferente para cada moneda
        // Ejemplo: mateo.nexus.mp (Pesos) / mateo.nexus.usd (Dólares)
        String sufijo = moneda.equals("ARS") ? ".mp" : ".usd";
        String aliasBase = "usuario.nuevo";
        if (user.getNombre() != null && !user.getNombre().isEmpty()) {
            aliasBase = user.getNombre().trim().toLowerCase().replace(" ", ".");
        }
        cuenta.setAlias(aliasBase + sufijo);

        cuentaRepository.save(cuenta);
    }

    private String generarCBU() {
        StringBuilder cbu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cbu.append((int) (Math.random() * 10));
        }
        return cbu.toString();
    }
}