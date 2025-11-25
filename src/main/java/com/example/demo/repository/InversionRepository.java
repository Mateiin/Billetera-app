package com.example.demo.repository;

import com.example.demo.model.Inversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InversionRepository extends JpaRepository<Inversion, Long> {
    
    List<Inversion> findByCuenta_Usuario_IdAndPagadoFalse(Long usuarioId);
    
    List<Inversion> findByPagadoFalse();
}