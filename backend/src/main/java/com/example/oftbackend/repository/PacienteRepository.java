package com.example.oftbackend.repository;

import com.example.oftbackend.model.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {
}
