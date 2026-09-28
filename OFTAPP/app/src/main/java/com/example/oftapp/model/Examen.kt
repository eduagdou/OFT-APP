package com.example.oftapp.model

data class Examen(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val descripcion: String,
    val estado: String,
    val resultado: Resultado? = null
)
