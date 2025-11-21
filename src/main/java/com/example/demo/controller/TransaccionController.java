package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Transaccion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.TransaccionRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private TransaccionRepository transaccionRepository;

    @GetMapping
    public List<Transaccion> obtenerMovimientos(@RequestParam String email) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user != null) {
            List<Cuenta> cuentas = cuentaRepository.findByUsuarioId(user.getId());
            if (!cuentas.isEmpty()) {
                // Buscamos los movimientos de su cuenta principal (la primera)
                return transaccionRepository.findByCuentaId(cuentas.get(0).getId());
            }
        }
        return new ArrayList<>(); // Si no hay nada, devolvemos lista vacía
    }
}