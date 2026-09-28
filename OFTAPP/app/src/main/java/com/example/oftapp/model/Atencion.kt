package com.example.oftapp.model

data class Atencion(
    val id: Int,
    val fecha: String,
    val motivo: String,
    val pacienteId: Int
)
