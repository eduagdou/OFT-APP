package com.example.oftapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.model.Atencion
import com.example.oftapp.model.Examen
import com.example.oftapp.model.Paciente
import com.example.oftapp.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExamenesViewModel : ViewModel() {

    // Datos ficticios escritos en el código (sin base de datos en esta etapa)
    val paciente = Paciente(1, "Camila Rojas", 34, "Miopía leve")

    val proximaAtencion = Atencion(1, "15/10/2026", "Control anual de la vista", 1)

    private val _examenes = MutableStateFlow(
        listOf(
            Examen(
                1, "Agudeza visual", "02/09/2026",
                "Mide qué tan bien se ve a distintas distancias.",
                "Realizado",
                Resultado(1, "Ojo derecho 20/25 · Ojo izquierdo 20/20", "Valores simulados, sin alteraciones.")
            ),
            Examen(
                2, "Presión intraocular", "02/09/2026",
                "Mide la presión dentro del ojo.",
                "Realizado",
                Resultado(2, "14 mmHg en ambos ojos", "Valor simulado, sin alteraciones.")
            ),
            Examen(3, "Fondo de ojo", "20/10/2026", "Revisa la retina y el nervio óptico.", "Pendiente"),
            Examen(4, "Campo visual", "05/11/2026", "Evalúa la visión periférica.", "Pendiente")
        )
    )
    val examenes: StateFlow<List<Examen>> = _examenes.asStateFlow()

    fun obtenerExamen(id: Int): Examen? = _examenes.value.firstOrNull { it.id == id }
}
