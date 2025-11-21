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

    // Clase auxiliar para recibir los datos de la transferencia
    static class TransferenciaRequest {
        public String emailOrigen;
        public String emailDestino;
        public java.math.BigDecimal monto;
    }

    @PostMapping("/transferencia")
    public String transferir(@RequestBody TransferenciaRequest request) {
        // 1. Validaciones básicas
        Usuario origen = usuarioRepository.findByEmail(request.emailOrigen);
        Usuario destino = usuarioRepository.findByEmail(request.emailDestino);

        if (origen == null || destino == null) return "ERROR_USUARIO";
        
        // Buscamos las cuentas
        Cuenta cuentaOrigen = cuentaRepository.findByUsuarioId(origen.getId()).get(0);
        Cuenta cuentaDestino = cuentaRepository.findByUsuarioId(destino.getId()).get(0);

        // 2. ¿Tiene saldo suficiente? ( saldo < monto )
        if (cuentaOrigen.getSaldo().compareTo(request.monto) < 0) {
            return "ERROR_SALDO";
        }

        // 3. LA MAGIA: Restar a uno y Sumar al otro
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(request.monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(request.monto));
        
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        // 4. Generar el historial (Solo generamos el gasto para el que envía por ahora)
        Transaccion t = new Transaccion();
        t.setDescripcion("Transferencia a " + destino.getNombre());
        t.setMonto(request.monto);
        t.setTipo("GASTO");
        t.setCuenta(cuentaOrigen);
        transaccionRepository.save(t);

        return "TRANSFERENCIA_EXITOSA";
    }
}