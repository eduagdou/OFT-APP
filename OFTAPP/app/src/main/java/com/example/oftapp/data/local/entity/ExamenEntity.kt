package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pacienteId: Long,
    val pacienteNombre: String,
    val medicoId: Long? = null,
    val medicoNombre: String? = null,
    val tipoExamen: String,
    val ojoEvaluado: String,
    val fechaExamen: String,
    val fechaResolucion: String? = null,
    val estado: String, // "Pendiente", "En Proceso", "Realizado"
    val comentarios: String? = null,
    val sucursal: String,
    val archivosAdjuntos: String? = null
)
