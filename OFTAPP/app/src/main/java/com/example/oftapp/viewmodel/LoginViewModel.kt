package com.example.oftapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    // Usuario ficticio (datos simulados)
    val usuario = Usuario(1, "Camila Rojas", "paciente@oftapp.cl", "1234")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun onClaveChange(valor: String) = _uiState.update { it.copy(clave = valor, error = null) }

    fun iniciarSesion(onExito: () -> Unit) {
        val s = _uiState.value
        if (s.correo.trim() == usuario.correo && s.clave == usuario.clave) {
            onExito()
        } else {
            _uiState.update { it.copy(error = "Correo o contraseña incorrectos") }
        }
    }

    fun limpiar() {
        _uiState.value = LoginUiState()
    }
}
