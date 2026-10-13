package com.example.oftapp.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: Rol = Rol.PACIENTE,
    val sucursalDefault: String = "Centro Oftalmológico Central"
)
