package com.example.oftbackend.controller;

import com.example.oftbackend.model.PacienteEntity;
import com.example.oftbackend.repository.PacienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes API", description = "Endpoints CRUD para la gestión de pacientes clínicos oftalmológicos")
public class PacienteController {

    private final PacienteRepository repository;

    public PacienteController(PacienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Obtener lista completa de pacientes")
    public List<PacienteEntity> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<PacienteEntity> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente")
    public PacienteEntity create(@RequestBody PacienteEntity paciente) {
        return repository.save(paciente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente existente")
    public ResponseEntity<PacienteEntity> update(@PathVariable Long id, @RequestBody PacienteEntity paciente) {
        return repository.findById(id).map(p -> {
            p.setNombre(paciente.getNombre());
            p.setApellidos(paciente.getApellidos());
            p.setEdad(paciente.getEdad());
            p.setPrevision(paciente.getPrevision());
            p.setTelefono(paciente.getTelefono());
            p.setCorreo(paciente.getCorreo());
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paciente por ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
