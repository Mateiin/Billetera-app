package com.example.demo.repository;

import com.example.demo.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    // Método personalizado para buscar todas las cuentas de un usuario específico
    List<Cuenta> findByUsuarioId(Long usuarioId);
}