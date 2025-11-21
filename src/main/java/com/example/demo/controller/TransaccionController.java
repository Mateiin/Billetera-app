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
    // 1. Clase auxiliar para recibir los datos del depósito
    static class TransaccionRequest {
        public String email;
        public java.math.BigDecimal monto;
    }

    // 2. El Endpoint para DEPOSITAR dinero
    @PostMapping("/deposito")
    public String depositar(@RequestBody TransaccionRequest request) {
        System.out.println("💰 Intento de depósito: " + request.monto + " para " + request.email);

        // A. Buscamos usuario y cuenta
        Usuario user = usuarioRepository.findByEmail(request.email);
        if (user == null) return "ERROR_USUARIO";
        
        List<Cuenta> cuentas = cuentaRepository.findByUsuarioId(user.getId());
        if (cuentas.isEmpty()) return "ERROR_CUENTA";
        
        Cuenta cuenta = cuentas.get(0); // Usamos la cuenta principal

        // B. Actualizamos el saldo (Saldo Actual + Depósito)
        cuenta.setSaldo(cuenta.getSaldo().add(request.monto));
        cuentaRepository.save(cuenta); // Guardamos el nuevo saldo en BD

        // C. Creamos el registro del movimiento (Para el historial)
        Transaccion t = new Transaccion();
        t.setDescripcion("Ingreso de Dinero");
        t.setMonto(request.monto);
        t.setTipo("INGRESO");
        t.setCuenta(cuenta); // Relacionamos con la cuenta
        transaccionRepository.save(t); // Guardamos la transacción en BD

        return "DEPOSITO_EXITOSO";
    }
}