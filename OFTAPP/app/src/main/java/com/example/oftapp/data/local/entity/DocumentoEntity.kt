package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documentos_examen")
data class DocumentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val examenId: Long,
    val nombreArchivo: String,
    val tipoArchivo: String,
    val tamanio: String,
    val fechaCarga: String,
    val contenidoSimulado: String? = null,
    val uriPath: String? = null
)
