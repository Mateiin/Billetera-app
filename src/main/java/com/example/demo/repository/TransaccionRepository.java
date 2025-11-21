package com.example.demo.repository;

import com.example.demo.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    // Buscar movimientos de una cuenta específica
    List<Transaccion> findByCuentaId(Long cuentaId);
}