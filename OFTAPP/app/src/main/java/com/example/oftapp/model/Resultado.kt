package com.example.oftapp.model

data class Resultado(
    val id: Int,
    val resumen: String,
    val observacion: String,
    val registradoPor: String = "Dr. Ricardo Sepúlveda (Oftalmólogo)",
    val fechaObservacion: String = "02/09/2026"
)
