package com.example.oftapp.model

enum class Rol(
    val titulo: String,
    val descripcion: String
) {
    ADMINISTRADOR(
        titulo = "Administrador Académico",
        descripcion = "Configura usuarios, roles, catálogos y consulta todos los registros de prueba."
    ),
    TECNOLOGO_MEDICO(
        titulo = "Tecnólogo Médico",
        descripcion = "Registra exámenes simulados, adjunta documentos y actualiza datos clínicos básicos."
    ),
    PERSONAL_ADMINISTRATIVO(
        titulo = "Personal Administrativo",
        descripcion = "Organiza antecedentes, gestiona el estado de entrega y filtra por sucursal o paciente."
    ),
    MEDICO_OFTALMOLOGO(
        titulo = "Médico Oftalmólogo",
        descripcion = "Revisa exámenes, consulta el historial clínico y registra observaciones de seguimiento."
    ),
    PACIENTE(
        titulo = "Paciente",
        descripcion = "Consulta sus resultados y su historial disponible en lenguaje claro (Solo lectura)."
    );

    fun puedeCrearAtencion(): Boolean =
        this == ADMINISTRADOR || this == PERSONAL_ADMINISTRATIVO || this == TECNOLOGO_MEDICO

    fun puedeCrearExamen(): Boolean =
        this == ADMINISTRADOR || this == TECNOLOGO_MEDICO

    fun puedeAdjuntarDocumentos(): Boolean =
        this == ADMINISTRADOR || this == TECNOLOGO_MEDICO

    fun puedeEditarObservacionMedica(): Boolean =
        this == ADMINISTRADOR || this == MEDICO_OFTALMOLOGO

    fun puedeActualizarEstadoEntrega(): Boolean =
        this == ADMINISTRADOR || this == PERSONAL_ADMINISTRATIVO

    fun esSoloLectura(): Boolean =
        this == PACIENTE
}
