package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inversiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVencimiento; // Cuándo se libera la plata

    private BigDecimal montoInvertido;
    private BigDecimal gananciaCalculada; // Cuánto va a ganar extra
    
    private boolean pagado; // false = dinero congelado, true = ya se le devolvió

    // Relación con la cuenta de donde salió la plata
    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;

    // Antes de guardar, configuramos las fechas automáticamente
    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.pagado = false;
    }
}