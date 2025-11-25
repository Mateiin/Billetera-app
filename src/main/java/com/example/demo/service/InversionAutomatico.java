package com.example.demo.service;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Inversion;
import com.example.demo.model.Transaccion;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.InversionRepository;
import com.example.demo.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component; // Importante

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class InversionAutomatico {

    @Autowired private InversionRepository inversionRepository;
    @Autowired private CuentaRepository cuentaRepository;
    @Autowired private TransaccionRepository transaccionRepository;

    // Se ejecuta automáticamente cada 5000 milisegundos (5 segundos)
    @Scheduled(fixedRate = 5000)
    public void revisarInversiones() {
        // 1. Buscamos todas las inversiones que NO han sido pagadas
        List<Inversion> inversionesPendientes = inversionRepository.findByPagadoFalse();

        LocalDateTime ahora = LocalDateTime.now();

        for (Inversion inv : inversionesPendientes) {
            // 2. Verificamos si ya venció (si la fecha actual es mayor a la de vencimiento)
            if (ahora.isAfter(inv.getFechaVencimiento())) {
                
                pagarInversion(inv);
            }
        }
    }

    private void pagarInversion(Inversion inv) {
        System.out.println("💰 PAGANDO INVERSIÓN ID: " + inv.getId());

        // A. Recuperamos la cuenta
        Cuenta cuenta = inv.getCuenta();
        
        // B. Calculamos total a devolver (Capital + Ganancia)
        BigDecimal total = inv.getMontoInvertido().add(inv.getGananciaCalculada());
        
        // C. Sumamos la plata a la cuenta
        cuenta.setSaldo(cuenta.getSaldo().add(total));
        cuentaRepository.save(cuenta);

        // D. Marcamos la inversión como PAGADA para no pagarla dos veces
        inv.setPagado(true);
        inversionRepository.save(inv);

        // E. Creamos el registro en el historial
        Transaccion t = new Transaccion();
        t.setDescripcion("Cobro Plazo Fijo + Interés");
        t.setMonto(total);
        t.setTipo("INGRESO");
        t.setCuenta(cuenta);
        transaccionRepository.save(t);
    }
}