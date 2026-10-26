package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.local.entity.PacienteEntity
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

    private val defaultPacientes = listOf(
        Paciente(1, "Camila Rojas", "18.432.109-K", 34, "+56 9 8765 4321", "Miopía leve (-1.5D OD, -1.25D OI), Astigmatismo"),
        Paciente(2, "Jorge Valenzuela", "12.876.543-2", 52, "+56 9 7654 3210", "Presbicia, Sospecha de Glaucoma"),
        Paciente(3, "María Teresa Soto", "9.876.543-1", 68, "+56 9 6543 2109", "Catarata senil bilateral, Retinopatía diabética no proliferativa")
    )

    private val _listaPacientesState = MutableStateFlow<List<Paciente>>(defaultPacientes)
    val listaPacientes: List<Paciente> get() = _listaPacientesState.value

    val pacienteActual: Paciente get() = _listaPacientesState.value.getOrElse(0) { defaultPacientes[0] }

    private val defaultExamenes = listOf(
        Examen(
            id = 1, pacienteId = 1, pacienteNombre = "Camila Rojas", atencionId = 4,
            tipoExamen = "Agudeza visual", ojoEvaluado = "Ambos Ojos (OU)", fecha = "02/09/2026",
            sucursal = "Sucursal Providencia", profesional = "TM. Andrea Morales", estado = "Realizado",
            estadoEntrega = "Disponible en portal", descripcion = "Mide la nitidez de la visión a distintas distancias con cartilla Snellen.",
            resultado = Resultado(1, "OD: 20/25 · OI: 20/20 · Visión binocular conservada", "Ligera miopía en ojo derecho. Se sugiere control anual con corrección óptica.", "Dr. Ricardo Sepúlveda", "02/09/2026"),
            documentos = listOf(
                DocumentoSimulado(101, "Informe_AgudezaVisual_CRojas.pdf", "PDF", "1.4 MB", "02/09/2026", "REPORTE CLÍNICO SIMULADO\nExamen: Agudeza Visual\nPaciente: Camila Rojas\nSnellen OD: 20/25, OI: 20/20")
            )
        ),
        Examen(
            id = 2, pacienteId = 1, pacienteNombre = "Camila Rojas", atencionId = 4,
            tipoExamen = "Presión intraocular", ojoEvaluado = "Ambos Ojos (OU)", fecha = "02/09/2026",
            sucursal = "Sucursal Providencia", profesional = "TM. Andrea Morales", estado = "Realizado",
            estadoEntrega = "Entregado en sucursal", descripcion = "Tonometría de aplanación para medir la presión dentro del globo ocular.",
            resultado = Resultado(2, "OD: 14 mmHg · OI: 15 mmHg", "Presión intraocular dentro de rangos normales (10-21 mmHg). Sin signos de hipertensión ocular.", "Dr. Ricardo Sepúlveda", "02/09/2026"),
            documentos = emptyList()
        ),
        Examen(
            id = 3, pacienteId = 1, pacienteNombre = "Camila Rojas", atencionId = 1,
            tipoExamen = "Fondo de ojo", ojoEvaluado = "Ambos Ojos (OU)", fecha = "20/10/2026",
            sucursal = "Sucursal Providencia", profesional = "Dr. Ricardo Sepúlveda", estado = "Pendiente",
            estadoEntrega = "Pendiente de firma", descripcion = "Evaluación detallada de la retina, mácula y nervio óptico bajo dilatación pupilar.",
            resultado = null, documentos = emptyList()
        ),
        Examen(
            id = 4, pacienteId = 2, pacienteNombre = "Jorge Valenzuela", atencionId = 2,
            tipoExamen = "Campo visual", ojoEvaluado = "Ojo Izquierdo (OI)", fecha = "05/11/2026",
            sucursal = "Sucursal Central", profesional = "TM. Andrea Morales", estado = "Pendiente",
            estadoEntrega = "Pendiente de firma", descripcion = "Campimetría computarizada para evaluar visión periférica y descartar glaucoma.",
            resultado = null, documentos = emptyList()
        )
    )

    private val _examenes = MutableStateFlow<List<Examen>>(defaultExamenes)
    val examenes: StateFlow<List<Examen>> = _examenes.asStateFlow()

    private val _formExamenState = MutableStateFlow(FormExamenState())
    val formExamenState: StateFlow<FormExamenState> = _formExamenState.asStateFlow()

    private val _syncState = MutableStateFlow(SyncUiState())
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerPacientes().collect { entities ->
                if (entities.isEmpty()) {
                    poblarPacientesDefault()
                } else {
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
                if (entities.isEmpty()) {
                    poblarExamenesDefault()
                } else {
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
    }

    private suspend fun poblarPacientesDefault() {
        val list = listOf(
            PacienteEntity(1, "18.432.109-K", "Camila", "Rojas", 34, "Fonasa", "+56 9 8765 4321", "paciente001@pacientes.com", "1234"),
            PacienteEntity(2, "12.876.543-2", "Jorge", "Valenzuela", 52, "Isapre", "+56 9 7654 3210", "jorge.valenzuela@pacientes.com", "1234"),
            PacienteEntity(3, "9.876.543-1", "María Teresa", "Soto", 68, "Fonasa", "+56 9 6543 2109", "mteresa.soto@pacientes.com", "1234")
        )
        repository.insertarPaciente(list[0])
        repository.insertarPaciente(list[1])
        repository.insertarPaciente(list[2])
    }

    private suspend fun poblarExamenesDefault() {
        val list = listOf(
            ExamenEntity(1, 1, "Camila Rojas", 3, "TM. Andrea Morales", "Agudeza visual", "Ambos Ojos (OU)", "02/09/2026", "02/09/2026", "Realizado", "OD: 20/25 · OI: 20/20", "Sucursal Providencia"),
            ExamenEntity(2, 1, "Camila Rojas", 3, "TM. Andrea Morales", "Presión intraocular", "Ambos Ojos (OU)", "02/09/2026", "02/09/2026", "Realizado", "OD: 14 mmHg · OI: 15 mmHg", "Sucursal Providencia"),
            ExamenEntity(3, 1, "Camila Rojas", 2, "Dr. Ricardo Sepúlveda", "Fondo de ojo", "Ambos Ojos (OU)", "20/10/2026", null, "Pendiente", "Evaluación de retina requerida", "Sucursal Providencia"),
            ExamenEntity(4, 2, "Jorge Valenzuela", 3, "TM. Andrea Morales", "Campo visual", "Ojo Izquierdo (OI)", "05/11/2026", null, "Pendiente", "Campimetría computarizada", "Sucursal Central")
        )
        repository.insertarExamen(list[0])
        repository.insertarExamen(list[1])
        repository.insertarExamen(list[2])
        repository.insertarExamen(list[3])
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
