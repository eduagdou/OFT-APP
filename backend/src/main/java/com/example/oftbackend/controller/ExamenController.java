package com.example.oftbackend.controller;

import com.example.oftbackend.model.ExamenEntity;
import com.example.oftbackend.repository.ExamenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@Tag(name = "Exámenes API", description = "Endpoints CRUD para la gestión y consulta de exámenes oftalmológicos")
public class ExamenController {

    private final ExamenRepository repository;

    public ExamenController(ExamenRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los exámenes")
    public List<ExamenEntity> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener examen por ID")
    public ResponseEntity<ExamenEntity> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Consultar historial de exámenes por ID de Paciente")
    public List<ExamenEntity> getByPaciente(@PathVariable Long pacienteId) {
        return repository.findByPacienteId(pacienteId);
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo examen clínico")
    public ExamenEntity create(@RequestBody ExamenEntity examen) {
        return repository.save(examen);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estado u observación de un examen")
    public ResponseEntity<ExamenEntity> update(@PathVariable Long id, @RequestBody ExamenEntity examen) {
        return repository.findById(id).map(e -> {
            e.setEstado(examen.getEstado());
            e.setComentarios(examen.getComentarios());
            e.setSincronizado(true);
            return ResponseEntity.ok(repository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }
}
