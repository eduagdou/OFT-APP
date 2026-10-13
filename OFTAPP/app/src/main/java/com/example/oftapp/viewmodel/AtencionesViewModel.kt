package com.example.oftapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.model.Atencion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FormAtencionState(
    val pacienteNombre: String = "",
    val fecha: String = "",
    val sucursal: String = "Sucursal Providencia",
    val profesional: String = "",
    val motivo: String = "",
    val error: String? = null
)

class AtencionesViewModel : ViewModel() {

    private val _atenciones = MutableStateFlow(
        listOf(
            Atencion(
                id = 1,
                pacienteId = 1,
                pacienteNombre = "Camila Rojas",
                fecha = "15/10/2026",
                sucursal = "Sucursal Providencia",
                profesional = "Dr. Ricardo Sepúlveda",
                motivo = "Control anual de agudeza visual",
                estado = "Programada"
            ),
            Atencion(
                id = 2,
                pacienteId = 2,
                pacienteNombre = "Jorge Valenzuela",
                fecha = "02/09/2026",
                sucursal = "Sucursal Central",
                profesional = "TM. Andrea Morales",
                motivo = "Evaluación de presión intraocular y tonometría",
                estado = "Completada"
            ),
            Atencion(
                id = 3,
                pacienteId = 3,
                pacienteNombre = "María Teresa Soto",
                fecha = "20/10/2026",
                sucursal = "Sucursal Las Condes",
                profesional = "Dr. Ricardo Sepúlveda",
                motivo = "Revisión de fondo de ojo por retinopatía",
                estado = "Programada"
            ),
            Atencion(
                id = 4,
                pacienteId = 1,
                pacienteNombre = "Camila Rojas",
                fecha = "02/09/2026",
                sucursal = "Sucursal Providencia",
                profesional = "TM. Andrea Morales",
                motivo = "Medición de vicio de refracción",
                estado = "Completada"
            )
        )
    )
    val atenciones: StateFlow<List<Atencion>> = _atenciones.asStateFlow()

    private val _formState = MutableStateFlow(FormAtencionState())
    val formState: StateFlow<FormAtencionState> = _formState.asStateFlow()

    fun onPacienteNombreChange(valStr: String) = _formState.update { it.copy(pacienteNombre = valStr, error = null) }
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

        val nuevaAtencion = Atencion(
            id = (_atenciones.value.maxOfOrNull { it.id } ?: 0) + 1,
            pacienteId = 1,
            pacienteNombre = s.pacienteNombre.trim(),
            fecha = s.fecha.trim(),
            sucursal = s.sucursal.trim(),
            profesional = s.profesional.trim(),
            motivo = s.motivo.trim(),
            estado = "Programada"
        )

        _atenciones.update { listOf(nuevaAtencion) + it }
        resetForm()
        return true
    }
}
