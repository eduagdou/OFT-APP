package com.example.oftapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.model.DocumentoSimulado
import com.example.oftapp.model.Examen
import com.example.oftapp.model.Paciente
import com.example.oftapp.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FormExamenState(
    val pacienteNombre: String = "Camila Rojas",
    val tipoExamen: String = "Agudeza visual",
    val ojoEvaluado: String = "Ambos Ojos (OU)", // "Ojo Derecho (OD)", "Ojo Izquierdo (OI)", "Ambos Ojos (OU)"
    val fecha: String = "",
    val sucursal: String = "Sucursal Providencia",
    val profesional: String = "TM. Andrea Morales",
    val estado: String = "Pendiente", // "Pendiente", "En Proceso", "Realizado"
    val descripcion: String = "",
    val observacionInicial: String = "",
    val error: String? = null
)

data class SyncUiState(
    val offlineDisponible: Boolean = true,
    val enSincronizacion: Boolean = false,
    val mensajeSync: String? = null
)

class ExamenesViewModel : ViewModel() {

    // Pacientes ficticios (RF06)
    val listaPacientes = listOf(
        Paciente(1, "Camila Rojas", "18.432.109-K", 34, "+56 9 8765 4321", "Miopía leve (-1.5D OD, -1.25D OI), Astigmatismo"),
        Paciente(2, "Jorge Valenzuela", "12.876.543-2", 52, "+56 9 7654 3210", "Presbicia, Sospecha de Glaucoma"),
        Paciente(3, "María Teresa Soto", "9.876.543-1", 68, "+56 9 6543 2109", "Catarata senil bilateral, Retinopatía diabética no proliferativa")
    )

    val pacienteActual: Paciente
        get() = listaPacientes[0]

    private val _examenes = MutableStateFlow(
        listOf(
            Examen(
                id = 1,
                pacienteId = 1,
                pacienteNombre = "Camila Rojas",
                atencionId = 4,
                tipoExamen = "Agudeza visual",
                ojoEvaluado = "Ambos Ojos (OU)",
                fecha = "02/09/2026",
                sucursal = "Sucursal Providencia",
                profesional = "TM. Andrea Morales",
                estado = "Realizado",
                estadoEntrega = "Disponible en portal",
                descripcion = "Mide la nitidez de la visión a distintas distancias con cartilla Snellen.",
                resultado = Resultado(
                    id = 1,
                    resumen = "OD: 20/25 · OI: 20/20 · Visión binocular conservada",
                    observacion = "Ligera miopía en ojo derecho. Se sugiere control anual con corrección óptica.",
                    registradoPor = "Dr. Ricardo Sepúlveda",
                    fechaObservacion = "02/09/2026"
                ),
                documentos = listOf(
                    DocumentoSimulado(
                        id = 101,
                        nombre = "Informe_AgudezaVisual_CRojas.pdf",
                        tipo = "PDF",
                        tamanio = "1.4 MB",
                        fechaCarga = "02/09/2026",
                        contenidoSimulado = "REPORTE CLÍNICO SIMULADO\nExamen: Agudeza Visual\nPaciente: Camila Rojas\nSnellen OD: 20/25, OI: 20/20"
                    ),
                    DocumentoSimulado(
                        id = 102,
                        nombre = "Topografia_Corneal_Simulada.jpg",
                        tipo = "Imagen",
                        tamanio = "850 KB",
                        fechaCarga = "02/09/2026",
                        contenidoSimulado = "[Mapa Topográfico Simulado de Curvatura Corneal Normal]"
                    )
                )
            ),
            Examen(
                id = 2,
                pacienteId = 1,
                pacienteNombre = "Camila Rojas",
                atencionId = 4,
                tipoExamen = "Presión intraocular",
                ojoEvaluado = "Ambos Ojos (OU)",
                fecha = "02/09/2026",
                sucursal = "Sucursal Providencia",
                profesional = "TM. Andrea Morales",
                estado = "Realizado",
                estadoEntrega = "Entregado en sucursal",
                descripcion = "Tonometría de aplanación para medir la presión dentro del globo ocular.",
                resultado = Resultado(
                    id = 2,
                    resumen = "OD: 14 mmHg · OI: 15 mmHg",
                    observacion = "Presión intraocular dentro de rangos normales (10-21 mmHg). Sin signos de hipertensión ocular.",
                    registradoPor = "Dr. Ricardo Sepúlveda",
                    fechaObservacion = "02/09/2026"
                ),
                documentos = listOf(
                    DocumentoSimulado(
                        id = 103,
                        nombre = "Curva_Tension_Tonometria.pdf",
                        tipo = "PDF",
                        tamanio = "920 KB",
                        fechaCarga = "02/09/2026",
                        contenidoSimulado = "CURVA DE TENSIÓN SIMULADA\nLectura 09:00: OD 14 mmHg, OI 15 mmHg"
                    )
                )
            ),
            Examen(
                id = 3,
                pacienteId = 1,
                pacienteNombre = "Camila Rojas",
                atencionId = 1,
                tipoExamen = "Fondo de ojo",
                ojoEvaluado = "Ambos Ojos (OU)",
                fecha = "20/10/2026",
                sucursal = "Sucursal Providencia",
                profesional = "Dr. Ricardo Sepúlveda",
                estado = "Pendiente",
                estadoEntrega = "Pendiente de firma",
                descripcion = "Evaluación detallada de la retina, mácula y nervio óptico bajo dilatación pupilar.",
                resultado = null,
                documentos = emptyList()
            ),
            Examen(
                id = 4,
                pacienteId = 2,
                pacienteNombre = "Jorge Valenzuela",
                atencionId = 2,
                tipoExamen = "Campo visual",
                ojoEvaluado = "Ojo Izquierdo (OI)",
                fecha = "05/11/2026",
                sucursal = "Sucursal Central",
                profesional = "TM. Andrea Morales",
                estado = "Pendiente",
                estadoEntrega = "Pendiente de firma",
                descripcion = "Campimetría computarizada para evaluar visión periférica y descartar glaucoma.",
                resultado = null,
                documentos = emptyList()
            )
        )
    )
    val examenes: StateFlow<List<Examen>> = _examenes.asStateFlow()

    private val _formExamenState = MutableStateFlow(FormExamenState())
    val formExamenState: StateFlow<FormExamenState> = _formExamenState.asStateFlow()

    private val _syncState = MutableStateFlow(SyncUiState())
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    // Form handlers
    fun onPacienteNombreChange(valStr: String) = _formExamenState.update { it.copy(pacienteNombre = valStr, error = null) }
    fun onTipoExamenChange(valStr: String) = _formExamenState.update { it.copy(tipoExamen = valStr, error = null) }
    fun onOjoEvaluadoChange(valStr: String) = _formExamenState.update { it.copy(ojoEvaluado = valStr, error = null) }
    fun onFechaChange(valStr: String) = _formExamenState.update { it.copy(fecha = valStr, error = null) }
    fun onSucursalChange(valStr: String) = _formExamenState.update { it.copy(sucursal = valStr, error = null) }
    fun onProfesionalChange(valStr: String) = _formExamenState.update { it.copy(profesional = valStr, error = null) }
    fun onEstadoChange(valStr: String) = _formExamenState.update { it.copy(estado = valStr, error = null) }
    fun onDescripcionChange(valStr: String) = _formExamenState.update { it.copy(descripcion = valStr, error = null) }
    fun onObservacionInicialChange(valStr: String) = _formExamenState.update { it.copy(observacionInicial = valStr, error = null) }

    fun resetFormExamen() {
        _formExamenState.value = FormExamenState()
    }

    // RF03 & RF05: Crear nuevo examen con validaciones obligatorias
    fun crearExamen(): Boolean {
        val s = _formExamenState.value
        if (s.pacienteNombre.isBlank()) {
            _formExamenState.update { it.copy(error = "El paciente es obligatorio") }
            return false
        }
        if (s.tipoExamen.isBlank()) {
            _formExamenState.update { it.copy(error = "El tipo de examen es obligatorio") }
            return false
        }
        if (s.ojoEvaluado.isBlank()) {
            _formExamenState.update { it.copy(error = "Debe especificar el ojo evaluado") }
            return false
        }
        if (s.fecha.isBlank()) {
            _formExamenState.update { it.copy(error = "La fecha del examen es obligatoria") }
            return false
        }
        if (s.sucursal.isBlank()) {
            _formExamenState.update { it.copy(error = "La sucursal es obligatoria") }
            return false
        }
        if (s.descripcion.isBlank()) {
            _formExamenState.update { it.copy(error = "La descripción del examen es obligatoria") }
            return false
        }

        val nuevoId = (_examenes.value.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoResultado = if (s.observacionInicial.isNotBlank()) {
            Resultado(
                id = nuevoId * 10,
                resumen = "Examen preliminar registrado",
                observacion = s.observacionInicial.trim(),
                registradoPor = s.profesional,
                fechaObservacion = s.fecha
            )
        } else null

        val nuevoExamen = Examen(
            id = nuevoId,
            pacienteId = 1,
            pacienteNombre = s.pacienteNombre.trim(),
            tipoExamen = s.tipoExamen.trim(),
            ojoEvaluado = s.ojoEvaluado.trim(),
            fecha = s.fecha.trim(),
            sucursal = s.sucursal.trim(),
            profesional = s.profesional.trim(),
            estado = s.estado,
            estadoEntrega = if (s.estado == "Realizado") "Disponible en portal" else "Pendiente de firma",
            descripcion = s.descripcion.trim(),
            resultado = nuevoResultado,
            documentos = emptyList()
        )

        _examenes.update { listOf(nuevoExamen) + it }
        resetFormExamen()
        return true
    }

    // RF04: Adjuntar/Cargar documento simulado a un examen
    fun adjuntarDocumentoSimulado(examenId: Int, nombreArchivo: String, tipoArchivo: String) {
        val nuevoDocId = (System.currentTimeMillis() % 100000).toInt()
        val nuevoDoc = DocumentoSimulado(
            id = nuevoDocId,
            nombre = nombreArchivo,
            tipo = tipoArchivo,
            tamanio = "1.1 MB",
            fechaCarga = "Hoy",
            contenidoSimulado = "Documento adjuntado simulado: $nombreArchivo ($tipoArchivo)\nFecha de sincronización: Hoy"
        )

        _examenes.update { lista ->
            lista.map { ex ->
                if (ex.id == examenId) {
                    ex.copy(documentos = ex.documentos + nuevoDoc)
                } else ex
            }
        }
    }

    // RF08: Actualización controlada por Médico Oftalmólogo (Observaciones)
    fun actualizarObservacionMedica(examenId: Int, resumen: String, observacion: String, profesionalNombre: String) {
        _examenes.update { lista ->
            lista.map { ex ->
                if (ex.id == examenId) {
                    val actualRes = ex.resultado
                    val resActualizado = Resultado(
                        id = actualRes?.id ?: (examenId * 100),
                        resumen = resumen.ifBlank { actualRes?.resumen ?: "Resultados dentro de parámetro" },
                        observacion = observacion,
                        registradoPor = profesionalNombre,
                        fechaObservacion = "Hoy"
                    )
                    ex.copy(resultado = resActualizado, estado = "Realizado")
                } else ex
            }
        }
    }

    // RF08: Actualización controlada por Personal Administrativo (Estado de entrega)
    fun actualizarEstadoEntrega(examenId: Int, nuevoEstadoEntrega: String) {
        _examenes.update { lista ->
            lista.map { ex ->
                if (ex.id == examenId) {
                    ex.copy(estadoEntrega = nuevoEstadoEntrega)
                } else ex
            }
        }
    }

    // RF10: Simular sincronización con API de prueba / Descarga offline
    fun simularSincronizacion() {
        _syncState.update { it.copy(enSincronizacion = true, mensajeSync = "Conectando a la API de prueba...") }
        // Simular éxito inmediato
        _syncState.update {
            it.copy(
                enSincronizacion = false,
                mensajeSync = "Sincronización exitosa con la API de prueba (Servidor Central)"
            )
        }
    }

    fun limpiarMensajeSync() {
        _syncState.update { it.copy(mensajeSync = null) }
    }

    fun obtenerExamen(id: Int): Examen? = _examenes.value.firstOrNull { it.id == id }

    // RF06: Consultar historial de exámenes por paciente
    fun obtenerHistorialPaciente(pacienteNombre: String): List<Examen> {
        return _examenes.value.filter {
            it.pacienteNombre.equals(pacienteNombre, ignoreCase = true)
        }
    }
}
