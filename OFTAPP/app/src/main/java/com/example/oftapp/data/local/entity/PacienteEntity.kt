package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class PacienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rut: String,
    val nombre: String,
    val apellidos: String,
    val edad: Int,
    val prevision: String, // "Fonasa", "Isapre", "Particular"
    val telefono: String,
    val correo: String,
    val clave: String
)
