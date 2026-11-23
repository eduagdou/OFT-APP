package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.local.entity.AtencionEntity
import com.example.oftapp.data.repository.AppRepository
import androidx.lifecycle.ViewModel
import com.example.oftapp.model.Atencion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormAtencionState(
    val pacienteNombre: String = "",
    val pacienteRut: String = "",
    val fecha: String = "",
    val sucursal: String = "Sucursal Providencia",
    val profesional: String = "",
    val motivo: String = "",
    val error: String? = null
)

class AtencionesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.pacienteDao(), db.usuarioDao(), db.examenDao(), db.documentoDao(), db.atencionDao())

    private val _atenciones = MutableStateFlow<List<Atencion>>(emptyList())
    val atenciones: StateFlow<List<Atencion>> = _atenciones.asStateFlow()

    private val _formState = MutableStateFlow(FormAtencionState())
    val formState: StateFlow<FormAtencionState> = _formState.asStateFlow()

    fun onPacienteNombreChange(valStr: String) = _formState.update { it.copy(pacienteNombre = valStr, error = null) }
    fun onPacienteRutChange(valStr: String) = _formState.update { it.copy(pacienteRut = valStr, error = null) }
    fun onFechaChange(valStr: String) = _formState.update { it.copy(fecha = valStr, error = null) }
    fun onSucursalChange(valStr: String) = _formState.update { it.copy(sucursal = valStr, error = null) }
    fun onProfesionalChange(valStr: String) = _formState.update { it.copy(profesional = valStr, error = null) }
    fun onMotivoChange(valStr: String) = _formState.update { it.copy(motivo = valStr, error = null) }

    fun resetForm() {
        _formState.value = FormAtencionState()
    }

    // RF05: Validar campos obligatorios antes de guardar
    fun registrarAtencion(): Boolean {
        val s = _formState.value
        if (s.pacienteNombre.isBlank()) {
            _formState.update { it.copy(error = "El nombre del paciente es obligatorio") }
            return false
        }
        if (s.fecha.isBlank()) {
            _formState.update { it.copy(error = "La fecha de la atención es obligatoria") }
            return false
        }
        if (s.sucursal.isBlank()) {
            _formState.update { it.copy(error = "Debe indicar la sucursal de atención") }
            return false
        }
        if (s.profesional.isBlank()) {
            _formState.update { it.copy(error = "El profesional a cargo es obligatorio") }
            return false
        }
        if (s.motivo.isBlank()) {
            _formState.update { it.copy(error = "El motivo de la atención es obligatorio") }
            return false
        }
        if (s.pacienteRut.isBlank()) {
            _formState.update { it.copy(error = "El RUT del paciente es obligatorio") }
            return false
        }

        val nuevaAtencion = Atencion(
            id = (_atenciones.value.maxOfOrNull { it.id } ?: 0) + 1,
            pacienteId = 4,
            pacienteNombre = s.pacienteNombre.trim(),
            fecha = s.fecha.trim(),
            sucursal = s.sucursal.trim(),
            profesional = s.profesional.trim(),
            motivo = s.motivo.trim(),
            estado = "Programada"
        )

        viewModelScope.launch {
            val paciente = repository.obtenerPacientePorRut(s.pacienteRut)
            if (paciente == null) {
                _formState.update { it.copy(error = "No existe un paciente registrado con ese RUT") }
                return@launch
            }

            val creada = nuevaAtencion.copy(
                pacienteId = paciente.id.toInt(),
                pacienteNombre = "${paciente.nombre} ${paciente.apellidos}".trim()
            )

            repository.insertarAtencion(AtencionEntity(
                id = creada.id.toLong(),
                pacienteId = creada.pacienteId.toLong(),
                pacienteNombre = creada.pacienteNombre,
                medicoNombre = creada.profesional,
                sucursal = creada.sucursal,
                fecha = creada.fecha,
                motivo = creada.motivo,
                estado = creada.estado
            ))

            _atenciones.update { listOf(creada) + it }
            resetForm()
        }

        return true
    }
}
