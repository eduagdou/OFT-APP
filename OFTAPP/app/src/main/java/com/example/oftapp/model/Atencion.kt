package com.example.oftapp.model

data class Atencion(
    val id: Int,
    val pacienteId: Int,
    val pacienteNombre: String,
    val fecha: String,
    val sucursal: String,
    val profesional: String,
    val motivo: String,
    val estado: String = "Programada" // "Programada", "En Atención", "Completada"
)
