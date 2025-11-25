package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/datos")
    public Map<String, Object> getDatosCuenta(@RequestParam String email) {
        Map<String, Object> respuesta = new HashMap<>();
        Usuario user = usuarioRepository.findByEmail(email);

        if (user != null) {
            // Buscamos la cuenta ARS
            Optional<Cuenta> cuentaArs = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "ARS");
            if (cuentaArs.isPresent()) {
                respuesta.put("saldoArs", cuentaArs.get().getSaldo());
                respuesta.put("cbuArs", cuentaArs.get().getCbu());
                respuesta.put("aliasArs", cuentaArs.get().getAlias());
            }

            // Buscamos la cuenta USD
            Optional<Cuenta> cuentaUsd = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "USD");
            if (cuentaUsd.isPresent()) {
                respuesta.put("saldoUsd", cuentaUsd.get().getSaldo());
                respuesta.put("cbuUsd", cuentaUsd.get().getCbu());
                respuesta.put("aliasUsd", cuentaUsd.get().getAlias());
            } else {
                // Si es un usuario viejo que no tiene USD, devolvemos 0
                respuesta.put("saldoUsd", BigDecimal.ZERO);
            }
        }
        return respuesta;
    }
}