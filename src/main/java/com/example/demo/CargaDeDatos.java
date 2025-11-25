package com.example.demo;

import com.example.demo.model.Cuenta;
import com.example.demo.model.Transaccion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CuentaRepository;
import com.example.demo.repository.TransaccionRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CargaDeDatos implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;

    // Inyectamos los 3 repositorios
    public CargaDeDatos(UsuarioRepository usuarioRepository, 
                        CuentaRepository cuentaRepository,
                        TransaccionRepository transaccionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // PREGUNTA CLAVE: ¿Ya hay usuarios en la base de datos?
        if (usuarioRepository.count() == 0) {
            
            System.out.println("--------------------------------");
            System.out.println("🚀 BASE DE DATOS VACÍA: INICIANDO CARGA...");
            System.out.println("--------------------------------");
            // 1. Crear Usuario
            Usuario user = new Usuario();
            user.setNombre("Estudiante Programacion");
            user.setPassword("123456");
            usuarioRepository.save(user);

            // 2. Crear Cuenta
            Cuenta cuenta = new Cuenta();
            cuenta.setNombre("Cuenta Principal");
            cuenta.setMoneda("ARS");
            cuenta.setSaldo(new BigDecimal("50000.00"));
            cuenta.setUsuario(user);
            cuentaRepository.save(cuenta);

            // 3. Crear Transacciones (Movimientos)
        
            // Gasto 1
            Transaccion t1 = new Transaccion();
            t1.setDescripcion("Suscripción Netflix");
            t1.setMonto(new BigDecimal("4500.00"));
            t1.setTipo("GASTO");
            t1.setCuenta(cuenta);
            transaccionRepository.save(t1);

            // Gasto 2
            Transaccion t2 = new Transaccion();
            t2.setDescripcion("Compra Supermercado");
            t2.setMonto(new BigDecimal("12300.50"));
            t2.setTipo("GASTO");
            t2.setCuenta(cuenta);
            transaccionRepository.save(t2);

            // Ingreso
            Transaccion t3 = new Transaccion();
            t3.setDescripcion("Transferencia Amigo");
            t3.setMonto(new BigDecimal("2000.00"));
            t3.setTipo("INGRESO");
            t3.setCuenta(cuenta);
            transaccionRepository.save(t3);

            // --- NUEVO USUARIO (EL AMIGO) ---
            Usuario user2 = new Usuario();
            user2.setNombre("Amigo Destinatario");
            user2.setEmail("amigo@fintech.com"); // <--- A este email le transferirás
            user2.setPassword("123456");
            usuarioRepository.save(user2);

            Cuenta cuenta2 = new Cuenta();
            cuenta2.setNombre("Cuenta de Amigo");
            cuenta2.setMoneda("ARS");
            cuenta2.setSaldo(new BigDecimal("1000.00")); // Empieza con poquito
            cuenta2.setUsuario(user2);
            cuentaRepository.save(cuenta2);

        System.out.println("🏁 CARGA FINALIZADA CON ÉXITO");
            
        } else {
            System.out.println("--------------------------------");
            System.out.println("✅ LA BASE DE DATOS YA TIENE DATOS. SALTANDO CARGA.");
            System.out.println("--------------------------------");
        }
    }
}