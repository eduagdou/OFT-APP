package com.example.oftapp.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val EXAMENES = "examenes"
    const val DETALLE = "detalle/{examenId}"
    const val PERFIL = "perfil"

    fun detalle(id: Int) = "detalle/$id"
}
