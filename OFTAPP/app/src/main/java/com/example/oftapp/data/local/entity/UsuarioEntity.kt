package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rut: String,
    val nombre: String,
    val apellidos: String,
    val edad: Int? = null,
    val telefono: String,
    val correo: String,
    val clave: String,
    val rol: String, // "MEDICO_OFTALMOLOGO", "TECNOLOGO_MEDICO", "PERSONAL_ADMINISTRATIVO", "ADMINISTRADOR"
    val sucursal: String,
    val fotoPerfilUri: String? = null
)
