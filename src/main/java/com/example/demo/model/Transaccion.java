package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Guarda fecha y hora exacta (ej: 2023-11-20T14:30:00)
    private LocalDateTime fecha; 

    private String tipo; // "INGRESO" o "GASTO"

    private String descripcion; // Ej: "Compra en Supermercado"

    @Column(precision = 19, scale = 2)
    private BigDecimal monto;

    // RELACIÓN CRÍTICA:
    // Una transacción pertenece a UNA cuenta específica.
    // Si borras la cuenta, se deberían borrar sus transacciones (lógica de cascada).
    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
    
    // Un pequeño truco: Antes de guardar, asignamos la fecha actual si viene vacía
    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}