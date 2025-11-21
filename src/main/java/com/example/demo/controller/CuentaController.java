package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Ejemplo de llamada: /api/cuentas/saldo?email=admin@fintech.com
    @GetMapping("/saldo")
    public BigDecimal getSaldo(@RequestParam String email) {
        System.out.println("💰 Solicitud de saldo para: " + email);

        // 1. Buscamos al usuario por su email
        Usuario user = usuarioRepository.findByEmail(email);

        if (user != null) {
            // 2. Buscamos sus cuentas usando su ID
            List<Cuenta> cuentas = cuentaRepository.findByUsuarioId(user.getId());

            // 3. Si tiene cuentas, devolvemos el saldo de la primera que encontremos
            if (!cuentas.isEmpty()) {
                return cuentas.get(0).getSaldo();
            }
        }
        return BigDecimal.ZERO; // Si no encontramos nada, decimos que tiene 0
    }
}