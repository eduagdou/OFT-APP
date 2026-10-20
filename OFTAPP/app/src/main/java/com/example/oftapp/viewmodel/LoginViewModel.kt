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

    private val _listaUsuarios = MutableStateFlow(
        listOf(
            Usuario(1, "Camila Rojas", "paciente001@pacientes.com", "1234", Rol.PACIENTE, "Sucursal Providencia"),
            Usuario(2, "Dr. Ricardo Sepúlveda", "medico001@empleados.com", "1234", Rol.MEDICO_OFTALMOLOGO, "Sucursal Central"),
            Usuario(3, "TM. Andrea Morales", "tecmed001@empleados.com", "1234", Rol.TECNOLOGO_MEDICO, "Sucursal Providencia"),
            Usuario(4, "Carlos Fuentes", "adminsec001@empleados.com", "1234", Rol.PERSONAL_ADMINISTRATIVO, "Sucursal Central"),
            Usuario(5, "Prof. Manuel Silva", "admin001@admin.com", "1234", Rol.ADMINISTRADOR, "Todas las sucursales")
        )
    )
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    // Propiedad calculada para compatibilidad con vistas existentes
    val usuariosFicticios: List<Usuario> get() = _listaUsuarios.value

    private val _usuarioLogueado = MutableStateFlow<Usuario>(_listaUsuarios.value[0])
    val usuarioLogueado: StateFlow<Usuario> = _usuarioLogueado.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun onClaveChange(valor: String) = _uiState.update { it.copy(clave = valor, error = null) }

    fun seleccionarUsuarioDemo(u: Usuario) {
        val usuarioActualizado = _listaUsuarios.value.firstOrNull { it.id == u.id } ?: u
        _uiState.update { it.copy(correo = usuarioActualizado.correo, clave = usuarioActualizado.clave, error = null) }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val s = _uiState.value
        val correoClean = s.correo.trim().lowercase()
        val userMatch = _listaUsuarios.value.firstOrNull { it.correo.lowercase() == correoClean }

        if (userMatch != null && s.clave == userMatch.clave) {
            _usuarioLogueado.value = userMatch
            _uiState.update { it.copy(error = null) }
            onExito()
        } else {
            _uiState.update { it.copy(error = "Credenciales incorrectas. Verifique el email ingresado o la contraseña.") }
        }
    }

    fun cerrarSesion() {
        val actual = _usuarioLogueado.value
        val userActualizado = _listaUsuarios.value.firstOrNull { it.id == actual.id } ?: _listaUsuarios.value[0]
        _usuarioLogueado.value = userActualizado
        _uiState.value = LoginUiState(correo = userActualizado.correo, clave = userActualizado.clave)
    }

    fun actualizarNombre(nuevoNombre: String) {
        val nombreLimpio = nuevoNombre.trim()
        if (nombreLimpio.isBlank()) return

        val idActual = _usuarioLogueado.value.id
        _listaUsuarios.update { lista ->
            lista.map { if (it.id == idActual) it.copy(nombre = nombreLimpio) else it }
        }
        _usuarioLogueado.update { it.copy(nombre = nombreLimpio) }
    }

    fun actualizarFotoPerfil(fotoUri: String?) {
        val idActual = _usuarioLogueado.value.id
        _listaUsuarios.update { lista ->
            lista.map { if (it.id == idActual) it.copy(fotoPerfilUri = fotoUri) else it }
        }
        _usuarioLogueado.update { it.copy(fotoPerfilUri = fotoUri) }
    }

    fun cambiarClave(claveActual: String, nuevaClave: String): String? {
        val usuarioActual = _usuarioLogueado.value

        if (claveActual != usuarioActual.clave) {
            return "La contraseña actual es incorrecta."
        }
        if (nuevaClave.trim().length < 4) {
            return "La nueva contraseña debe tener al menos 4 caracteres."
        }
        if (nuevaClave == claveActual) {
            return "La nueva contraseña debe ser diferente a la contraseña actual."
        }

        val idActual = usuarioActual.id
        val claveLimpia = nuevaClave.trim()

        _listaUsuarios.update { lista ->
            lista.map { if (it.id == idActual) it.copy(clave = claveLimpia) else it }
        }
        _usuarioLogueado.update { it.copy(clave = claveLimpia) }

        return null
    }
}
