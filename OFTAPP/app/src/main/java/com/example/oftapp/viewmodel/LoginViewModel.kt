package com.example.oftapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val correo: String = "paciente001@pacientes.com",
    val clave: String = "1234",
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    // Lista de usuarios ficticios representativos de cada rol con emails especificos
    val usuariosFicticios = listOf(
        Usuario(1, "Camila Rojas", "paciente001@pacientes.com", "1234", Rol.PACIENTE, "Sucursal Providencia"),
        Usuario(2, "Dr. Ricardo Sepúlveda", "medico001@empleados.com", "1234", Rol.MEDICO_OFTALMOLOGO, "Sucursal Central"),
        Usuario(3, "TM. Andrea Morales", "tecmed001@empleados.com", "1234", Rol.TECNOLOGO_MEDICO, "Sucursal Providencia"),
        Usuario(4, "Carlos Fuentes", "adminsec001@empleados.com", "1234", Rol.PERSONAL_ADMINISTRATIVO, "Sucursal Central"),
        Usuario(5, "Prof. Manuel Silva", "admin001@admin.com", "1234", Rol.ADMINISTRADOR, "Todas las sucursales")
    )

    private val _usuarioLogueado = MutableStateFlow<Usuario>(usuariosFicticios[0])
    val usuarioLogueado: StateFlow<Usuario> = _usuarioLogueado.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun onClaveChange(valor: String) = _uiState.update { it.copy(clave = valor, error = null) }

    fun seleccionarUsuarioDemo(u: Usuario) {
        _uiState.update { it.copy(correo = u.correo, clave = u.clave, error = null) }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val s = _uiState.value
        val correoClean = s.correo.trim().lowercase()
        val userMatch = usuariosFicticios.firstOrNull { it.correo.lowercase() == correoClean }

        if (userMatch != null && s.clave == userMatch.clave) {
            _usuarioLogueado.value = userMatch
            _uiState.update { it.copy(error = null) }
            onExito()
        } else {
            _uiState.update { it.copy(error = "Credenciales incorrectas. Verifique el email ingresado o seleccione una cuenta demo.") }
        }
    }

    fun cerrarSesion() {
        _usuarioLogueado.value = usuariosFicticios[0]
        _uiState.value = LoginUiState()
    }
}
