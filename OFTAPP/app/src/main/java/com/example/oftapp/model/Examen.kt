package com.example.oftapp.model

data class Examen(
    val id: Int,
    val pacienteId: Int,
    val pacienteNombre: String,
    val atencionId: Int? = null,
    val tipoExamen: String, // ej. "Agudeza visual", "Presión intraocular", "Fondo de ojo", "Campo visual"
    val ojoEvaluado: String, // "Ojo Derecho (OD)", "Ojo Izquierdo (OI)", "Ambos Ojos (OU)"
    val fecha: String,
    val sucursal: String,
    val profesional: String,
    val estado: String, // "Pendiente", "En Proceso", "Realizado"
    val estadoEntrega: String = "Disponible en portal", // "Pendiente de firma", "Disponible en portal", "Entregado en sucursal"
    val descripcion: String,
    val resultado: Resultado? = null,
    val documentos: List<DocumentoSimulado> = emptyList()
)
