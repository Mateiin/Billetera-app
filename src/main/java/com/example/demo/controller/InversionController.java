package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Inversion;
import com.example.demo.model.Transaccion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.InversionRepository;
import com.example.demo.repository.TransaccionRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inversiones")
public class InversionController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private InversionRepository inversionRepository;
    @Autowired private TransaccionRepository transaccionRepository;

    // Datos para recibir el pedido
    static class InversionRequest {
        public String email;
        public BigDecimal monto;
        public int segundosSimulacion; // Cuánto tiempo real durará la inversión (ej: 60 seg)
    }

    // 1. CREAR UNA NUEVA INVERSIÓN
    @PostMapping("/crear")
    public String crearInversion(@RequestBody InversionRequest request) {
        Usuario user = usuarioRepository.findByEmail(request.email);
        if (user == null) return "ERROR_USUARIO";

        // Buscamos cuenta en PESOS
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "ARS");
        if (cuentaOpt.isEmpty()) return "ERROR_CUENTA";
        Cuenta cuenta = cuentaOpt.get();

        // Validar Saldo
        if (cuenta.getSaldo().compareTo(request.monto) < 0) return "ERROR_SALDO";

        // --- LÓGICA FINANCIERA ---
        // Simulamos una TNA (Tasa Nominal Anual) del 70%
        // Interés Mensual = 70% / 12 = 5.83%
        // USAMOS RoundingMode PARA EVITAR ERRORES DE DECIMALES INFINITOS
        BigDecimal interes = request.monto
                .multiply(new BigDecimal("0.0583"))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Descontamos la plata de la cuenta YA MISMO
        cuenta.setSaldo(cuenta.getSaldo().subtract(request.monto));
        cuentaRepository.save(cuenta);

        // Creamos la Inversión
        Inversion inversion = new Inversion();
        inversion.setMontoInvertido(request.monto);
        inversion.setGananciaCalculada(interes);
        inversion.setCuenta(cuenta);
        // Definimos cuándo vence (ej: en 60 segundos para probar rápido)
        inversion.setFechaVencimiento(LocalDateTime.now().plusSeconds(request.segundosSimulacion));
        
        inversionRepository.save(inversion);

        // Registramos el movimiento para que se vea en el historial
        Transaccion t = new Transaccion();
        t.setDescripcion("Constitución Plazo Fijo");
        t.setMonto(request.monto);
        t.setTipo("GASTO"); // Es un gasto porque la plata "sale" de la cuenta disponible
        t.setCuenta(cuenta);
        transaccionRepository.save(t);

        return "INVERSION_EXITOSA";
    }

    // 2. VER MIS INVERSIONES ACTIVAS
    @GetMapping("/lista")
    public List<Inversion> obtenerInversiones(@RequestParam String email) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user != null) {
            return inversionRepository.findByCuenta_Usuario_IdAndPagadoFalse(user.getId());
        }
        return List.of();
    }
}