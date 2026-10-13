package com.example.oftapp.model

data class DocumentoSimulado(
    val id: Int,
    val nombre: String,
    val tipo: String, // "PDF", "Imagen", "Tabla"
    val tamanio: String, // ej. "1.2 MB"
    val fechaCarga: String,
    val contenidoSimulado: String
)
