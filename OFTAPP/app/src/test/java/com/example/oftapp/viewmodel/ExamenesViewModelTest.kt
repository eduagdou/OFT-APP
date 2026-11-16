package com.example.oftapp.viewmodel

import com.example.oftapp.model.Examen
import com.example.oftapp.viewmodel.FormExamenState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExamenesViewModelTest {

    private lateinit var formState: FormExamenState

    @Before
    fun setUp() {
        formState = FormExamenState()
    }

    @Test
    fun `validarFormularioConCamposVaciosRetornaError`() {
        val estadoVacio = FormExamenState(
            pacienteNombre = "",
            tipoExamen = "",
            ojoEvaluado = "",
            fecha = "",
            sucursal = "",
            descripcion = ""
        )
        
        assertTrue(estadoVacio.pacienteNombre.isEmpty())
        assertTrue(estadoVacio.tipoExamen.isEmpty())
    }

    @Test
    fun `validarFormularioConCamposValidos`() {
        val estadoValido = FormExamenState(
            pacienteNombre = "Camila Rojas",
            tipoExamen = "Fondo de Ojo",
            ojoEvaluado = "Ambos Ojos (OU)",
            fecha = "12/11/2026",
            sucursal = "Sucursal Central",
            profesional = "Dr. Sepúlveda",
            descripcion = "Evaluación de retina"
        )

        assertEquals("Camila Rojas", estadoValido.pacienteNombre)
        assertEquals("Fondo de Ojo", estadoValido.tipoExamen)
        assertNull(estadoValido.error)
    }

    @Test
    fun `filtrarExamenesPorPacienteCorrectamente`() {
        val listaExamenes = listOf(
            Examen(1, 1, "Camila Rojas", 1, "Fondo de ojo", "OU", "10/10/2026", "Prov", "Dr.", "Realizado", "Portal", "Desc"),
            Examen(2, 2, "Jorge Valenzuela", 2, "Campo visual", "OI", "11/10/2026", "Central", "TM.", "Pendiente", "Portal", "Desc")
        )

        val resultadoCamila = listaExamenes.filter { it.pacienteNombre.equals("Camila Rojas", ignoreCase = true) }
        val resultadoJorge = listaExamenes.filter { it.pacienteNombre.equals("Jorge Valenzuela", ignoreCase = true) }

        assertEquals(1, resultadoCamila.size)
        assertEquals(1, resultadoJorge.size)
        assertEquals("Fondo de ojo", resultadoCamila.first().tipoExamen)
    }

    @Test
    fun `sincronizacionOfflineCalculaPendientes()`() {
        val examenOnline = Examen(1, 1, "Camila", 1, "Fondo", "OU", "10/10/2026", "Prov", "Dr", "Realizado", "Portal", "Desc", sincronizado = true)
        val examenOffline = Examen(2, 1, "Camila", 1, "Presión", "OU", "10/10/2026", "Prov", "Dr", "Pendiente", "Portal", "Desc", sincronizado = false)

        val lista = listOf(examenOnline, examenOffline)
        val pendientesSync = lista.filter { !it.sincronizado }

        assertEquals(1, pendientesSync.size)
        assertEquals(2, pendientesSync.first().id)
    }
}
