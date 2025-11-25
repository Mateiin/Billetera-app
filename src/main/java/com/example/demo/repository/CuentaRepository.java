package com.example.demo.repository;

import com.example.demo.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    
    List<Cuenta> findByUsuarioId(Long usuarioId);

    Optional<Cuenta> findByCbu(String cbu);
    Optional<Cuenta> findByAlias(String alias);

    // NUEVO: Buscar cuenta por Usuario Y por Moneda ("ARS" o "USD")
    Optional<Cuenta> findByUsuarioIdAndMoneda(Long usuarioId, String moneda);
}