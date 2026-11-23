package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "atenciones")
data class AtencionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pacienteId: Long = 1,
    val pacienteNombre: String,
    val medicoId: Long? = null,
    val medicoNombre: String? = null,
    val sucursalId: Long? = null,
    val sucursal: String,
    val fecha: String,
    val motivo: String,
    val estado: String = "Programada"
)
