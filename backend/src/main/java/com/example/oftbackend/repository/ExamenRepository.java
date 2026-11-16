package com.example.oftbackend.repository;

import com.example.oftbackend.model.ExamenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamenRepository extends JpaRepository<ExamenEntity, Long> {
    List<ExamenEntity> findByPacienteId(Long pacienteId);
    List<ExamenEntity> findByTipoExamenContainingIgnoreCase(String tipoExamen);
}
