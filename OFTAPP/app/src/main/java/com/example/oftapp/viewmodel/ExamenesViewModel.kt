package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.AppRepository
import com.example.oftapp.model.DocumentoSimulado
import com.example.oftapp.model.Examen
import com.example.oftapp.model.Paciente
import com.example.oftapp.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormExamenState(
    val pacienteNombre: String = "Camila Rojas",
    val tipoExamen: String = "Agudeza visual",
    val ojoEvaluado: String = "Ambos Ojos (OU)",
    val fecha: String = "",
    val sucursal: String = "Sucursal Providencia",
    val profesional: String = "TM. Andrea Morales",
    val estado: String = "Pendiente",
    val descripcion: String = "",
    val observacionInicial: String = "",
    val error: String? = null
)

data class SyncUiState(
    val offlineDisponible: Boolean = true,
    val enSincronizacion: Boolean = false,
    val mensajeSync: String? = null
)

class ExamenesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.pacienteDao(), db.usuarioDao(), db.examenDao())

    private val _listaPacientesState = MutableStateFlow<List<Paciente>>(
        listOf(
            Paciente(1, "Camila Rojas", "18.432.109-K", 34, "+56 9 8765 4321", "Miopía leve (-1.5D OD, -1.25D OI), Astigmatismo"),
            Paciente(2, "Jorge Valenzuela", "12.876.543-2", 52, "+56 9 7654 3210", "Presbicia, Sospecha de Glaucoma"),
            Paciente(3, "María Teresa Soto", "9.876.543-1", 68, "+56 9 6543 2109", "Catarata senil bilateral, Retinopatía diabética no proliferativa")
        )
    )

    val listaPacientes: List<Paciente> get() = _listaPacientesState.value

    val pacienteActual: Paciente get() = _listaPacientesState.value.getOrElse(0) {
        Paciente(1, "Camila Rojas", "18.432.109-K", 34, "+56 9 8765 4321", "Miopía leve")
    }

    private val _examenes = MutableStateFlow<List<Examen>>(emptyList())
    val examenes: StateFlow<List<Examen>> = _examenes.asStateFlow()

    private val _formExamenState = MutableStateFlow(FormExamenState())
    val formExamenState: StateFlow<FormExamenState> = _formExamenState.asStateFlow()

    private val _syncState = MutableStateFlow(SyncUiState())
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerPacientes().collect { entities ->
                if (entities.isNotEmpty()) {
                    _listaPacientesState.value = entities.map { p ->
                        Paciente(
                            id = p.id.toInt(),
                            nombre = "${p.nombre} ${p.apellidos}".trim(),
                            rutFicticio = p.rut,
                            edad = p.edad,
                            telefonoFicticio = p.telefono,
                            antecedentes = "${p.prevision} · ${p.correo}"
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.obtenerExamenes().collect { entities ->
                _examenes.value = entities.map { entity ->
                    val resultado = if (!entity.comentarios.isNullOrBlank()) {
                        Resultado(
                            id = entity.id.toInt() * 10,
                            resumen = entity.tipoExamen,
                            observacion = entity.comentarios,
                            registradoPor = entity.medicoNombre ?: "Profesional a cargo",
                            fechaObservacion = entity.fechaResolucion ?: entity.fechaExamen
                        )
                    } else null

                    Examen(
                        id = entity.id.toInt(),
                        pacienteId = entity.pacienteId.toInt(),
                        pacienteNombre = entity.pacienteNombre,
                        atencionId = 1,
                        tipoExamen = entity.tipoExamen,
                        ojoEvaluado = entity.ojoEvaluado,
                        fecha = entity.fechaExamen,
                        sucursal = entity.sucursal,
                        profesional = entity.medicoNombre ?: "Por asignar",
                        estado = entity.estado,
                        estadoEntrega = if (entity.estado == "Realizado") "Disponible en portal" else "Pendiente de firma",
                        descripcion = entity.comentarios ?: "Examen clínico registrado",
                        resultado = resultado,
                        documentos = emptyList()
                    )
                }
            }
        }
    }

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

        viewModelScope.launch {
            val nuevaEntidad = ExamenEntity(
                pacienteId = 1,
                pacienteNombre = s.pacienteNombre.trim(),
                medicoNombre = s.profesional.trim(),
                tipoExamen = s.tipoExamen.trim(),
                ojoEvaluado = s.ojoEvaluado.trim(),
                fechaExamen = s.fecha.trim(),
                fechaResolucion = if (s.estado == "Realizado") s.fecha.trim() else null,
                estado = s.estado,
                comentarios = s.descripcion.trim() + if (s.observacionInicial.isNotBlank()) "\nObservación: ${s.observacionInicial.trim()}" else "",
                sucursal = s.sucursal.trim()
            )
            repository.insertarExamen(nuevaEntidad)
        }

        resetFormExamen()
        return true
    }

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

    fun actualizarObservacionMedica(examenId: Int, resumen: String, observacion: String, profesionalNombre: String) {
        viewModelScope.launch {
            val actual = repository.obtenerExamenPorId(examenId.toLong())
            if (actual != null) {
                val obsFinal = "$resumen\n$observacion".trim()
                val actualizado = actual.copy(
                    comentarios = obsFinal,
                    medicoNombre = profesionalNombre,
                    estado = "Realizado",
                    fechaResolucion = "Hoy"
                )
                repository.actualizarExamen(actualizado)
            }
        }
    }

    fun actualizarEstadoEntrega(examenId: Int, nuevoEstadoEntrega: String) {
        _examenes.update { lista ->
            lista.map { ex ->
                if (ex.id == examenId) {
                    ex.copy(estadoEntrega = nuevoEstadoEntrega)
                } else ex
            }
        }
    }

    fun simularSincronizacion() {
        _syncState.update { it.copy(enSincronizacion = true, mensajeSync = "Conectando a la API de prueba...") }
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

    fun obtenerHistorialPaciente(pacienteNombre: String): List<Examen> {
        return _examenes.value.filter {
            it.pacienteNombre.equals(pacienteNombre, ignoreCase = true)
        }
    }
}
