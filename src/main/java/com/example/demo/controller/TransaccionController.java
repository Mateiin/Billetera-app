package com.example.demo.controller;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Transaccion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.TransaccionRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private TransaccionRepository transaccionRepository;

    // --- CLASES AUXILIARES ---
    static class TransaccionRequest {
        public String email;
        public BigDecimal monto;
    }

    static class TransferenciaRequest {
        public String emailOrigen;
        public String destinatario;
        public BigDecimal monto;
    }

    static class CompraDolarRequest {
        public String email;
        public BigDecimal montoDolares; // Cuantos dólares quiero
        public BigDecimal precioCotizacion; // A cuánto está el dólar hoy
    }

    // --- ENDPOINTS ---

    @GetMapping
    public List<Transaccion> obtenerMovimientos(@RequestParam String email) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user != null) {
            List<Cuenta> cuentas = cuentaRepository.findByUsuarioId(user.getId());
            if (!cuentas.isEmpty()) {
                // Devolvemos movimientos de la cuenta principal (ARS) por defecto
                // (Para hacerlo perfecto deberíamos unir movimientos de todas las cuentas, 
                // pero para este MVP usaremos la de ARS para el historial principal)
                return transaccionRepository.findByCuentaId(cuentas.get(0).getId());
            }
        }
        return new ArrayList<>();
    }

    @PostMapping("/deposito")
    public String depositar(@RequestBody TransaccionRequest request) {
        Usuario user = usuarioRepository.findByEmail(request.email);
        if (user == null) return "ERROR_USUARIO";
        
        // Buscamos la cuenta en ARS
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "ARS");
        if (cuentaOpt.isEmpty()) return "ERROR_CUENTA_ARS";
        
        Cuenta cuenta = cuentaOpt.get();
        cuenta.setSaldo(cuenta.getSaldo().add(request.monto));
        cuentaRepository.save(cuenta);

        Transaccion t = new Transaccion();
        t.setDescripcion("Ingreso de Dinero");
        t.setMonto(request.monto);
        t.setTipo("INGRESO");
        t.setCuenta(cuenta);
        transaccionRepository.save(t);

        return "DEPOSITO_EXITOSO";
    }

    @PostMapping("/transferencia")
    public String transferir(@RequestBody TransferenciaRequest request) {
        Usuario origen = usuarioRepository.findByEmail(request.emailOrigen);
        if (origen == null) return "ERROR_ORIGEN";
        
        // Asumimos transferencia en ARS por ahora
        Optional<Cuenta> cuentaOrigenOpt = cuentaRepository.findByUsuarioIdAndMoneda(origen.getId(), "ARS");
        if (cuentaOrigenOpt.isEmpty()) return "ERROR_CUENTA_ORIGEN";
        Cuenta cuentaOrigen = cuentaOrigenOpt.get();

        Cuenta cuentaDestino = null;

        // Búsqueda inteligente de destino
        if (request.destinatario.contains("@")) {
            Usuario usuarioDestino = usuarioRepository.findByEmail(request.destinatario);
            if (usuarioDestino != null) {
                cuentaDestino = cuentaRepository.findByUsuarioIdAndMoneda(usuarioDestino.getId(), "ARS").orElse(null);
            }
        } else if (request.destinatario.matches("[0-9]+")) {
             cuentaDestino = cuentaRepository.findByCbu(request.destinatario).orElse(null);
        }
        if (cuentaDestino == null) {
            cuentaDestino = cuentaRepository.findByAlias(request.destinatario).orElse(null);
        }

        if (cuentaDestino == null) return "ERROR_DESTINATARIO";
        if (cuentaOrigen.getId().equals(cuentaDestino.getId())) return "ERROR_MISMA_CUENTA";

        if (cuentaOrigen.getSaldo().compareTo(request.monto) < 0) return "ERROR_SALDO";

        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(request.monto));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(request.monto));
        
        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        Transaccion t = new Transaccion();
        String nombreDestino = cuentaDestino.getUsuario().getNombre();
        t.setDescripcion("Transferencia a " + nombreDestino);
        t.setMonto(request.monto);
        t.setTipo("GASTO");
        t.setCuenta(cuentaOrigen);
        transaccionRepository.save(t);

        return "TRANSFERENCIA_EXITOSA";
    }

    // --- NUEVO: COMPRA DE DÓLARES ---
    @PostMapping("/compra-dolar")
    public String comprarDolares(@RequestBody CompraDolarRequest request) {
        Usuario user = usuarioRepository.findByEmail(request.email);
        if (user == null) return "ERROR_USUARIO";

        // 1. Buscamos ambas cuentas
        Optional<Cuenta> cuentaArsOpt = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "ARS");
        Optional<Cuenta> cuentaUsdOpt = cuentaRepository.findByUsuarioIdAndMoneda(user.getId(), "USD");

        if (cuentaArsOpt.isEmpty() || cuentaUsdOpt.isEmpty()) return "ERROR_CUENTAS";

        Cuenta cuentaArs = cuentaArsOpt.get();
        Cuenta cuentaUsd = cuentaUsdOpt.get();

        // 2. Calculamos cuánto le va a costar en Pesos
        // Costo = Cantidad Dolares * Precio Cotizacion
        BigDecimal costoEnPesos = request.montoDolares.multiply(request.precioCotizacion);

        // 3. Verificamos si tiene pesos suficientes
        if (cuentaArs.getSaldo().compareTo(costoEnPesos) < 0) {
            return "ERROR_SALDO_ARS";
        }

        // 4. Ejecutamos el cambio
        cuentaArs.setSaldo(cuentaArs.getSaldo().subtract(costoEnPesos)); // Restamos Pesos
        cuentaUsd.setSaldo(cuentaUsd.getSaldo().add(request.montoDolares)); // Sumamos Dólares

        cuentaRepository.save(cuentaArs);
        cuentaRepository.save(cuentaUsd);

        // 5. Registramos el movimiento (Gasto en pesos)
        Transaccion t = new Transaccion();
        t.setDescripcion("Compra " + request.montoDolares + " USD");
        t.setMonto(costoEnPesos);
        t.setTipo("GASTO");
        t.setCuenta(cuentaArs);
        transaccionRepository.save(t);

        return "COMPRA_EXITOSA";
    }
}