package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Caja de Ahorro", "Billetera Física"

    // REGLA FINTECH: Siempre BigDecimal para dinero.
    // scale = 2 significa que guardaremos 2 decimales (centavos)
    @Column(precision = 19, scale = 2) 
    private BigDecimal saldo;

    private String moneda; // "ARS", "USD"

    // LA RELACIÓN:
    // Muchas cuentas (@Many...) pertenecen a Un usuario (...ToOne)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}