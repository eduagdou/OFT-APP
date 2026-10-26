package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.repository.AppRepository
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "paciente001@pacientes.com",
    val clave: String = "1234",
    val error: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.pacienteDao(), db.usuarioDao(), db.examenDao())

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    val usuariosFicticios: List<Usuario> get() = _listaUsuarios.value

    private val _usuarioLogueado = MutableStateFlow(
        Usuario(1, "Camila Rojas", "paciente001@pacientes.com", "1234", Rol.PACIENTE, "Sucursal Providencia")
    )
    val usuarioLogueado: StateFlow<Usuario> = _usuarioLogueado.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerUsuarios().collect { entities ->
                val usuarios = entities.map { entity ->
                    Usuario(
                        id = entity.id.toInt(),
                        nombre = "${entity.nombre} ${entity.apellidos}".trim(),
                        correo = entity.correo,
                        clave = entity.clave,
                        rol = parseRol(entity.rol),
                        sucursalDefault = entity.sucursal,
                        fotoPerfilUri = entity.fotoPerfilUri
                    )
                }
                _listaUsuarios.value = usuarios
                if (usuarios.isNotEmpty()) {
                    val actualId = _usuarioLogueado.value.id
                    val encontrado = usuarios.firstOrNull { it.id == actualId } ?: usuarios[0]
                    _usuarioLogueado.value = encontrado
                }
            }
        }
    }

    private fun parseRol(rolStr: String): Rol {
        return try {
            Rol.valueOf(rolStr)
        } catch (e: Exception) {
            Rol.PACIENTE
        }
    }

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
        val userActualizado = _listaUsuarios.value.firstOrNull { it.id == actual.id } ?: _listaUsuarios.value.getOrElse(0) { actual }
        _usuarioLogueado.value = userActualizado
        _uiState.value = LoginUiState(correo = userActualizado.correo, clave = userActualizado.clave)
    }

    fun actualizarNombre(nuevoNombre: String) {
        val nombreLimpio = nuevoNombre.trim()
        if (nombreLimpio.isBlank()) return

        val idActual = _usuarioLogueado.value.id
        viewModelScope.launch {
            val userEntity = repository.obtenerUsuarioPorId(idActual.toLong())
            if (userEntity != null) {
                val partes = nombreLimpio.split(" ", limit = 2)
                val pNombre = partes.getOrElse(0) { nombreLimpio }
                val pApellido = partes.getOrElse(1) { "" }
                repository.actualizarUsuario(userEntity.copy(nombre = pNombre, apellidos = pApellido))
            }
        }
    }

    fun actualizarFotoPerfil(fotoUri: String?) {
        val idActual = _usuarioLogueado.value.id
        viewModelScope.launch {
            val userEntity = repository.obtenerUsuarioPorId(idActual.toLong())
            if (userEntity != null) {
                repository.actualizarUsuario(userEntity.copy(fotoPerfilUri = fotoUri))
            }
        }
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

        viewModelScope.launch {
            val userEntity = repository.obtenerUsuarioPorId(idActual.toLong())
            if (userEntity != null) {
                repository.actualizarUsuario(userEntity.copy(clave = claveLimpia))
            }
        }

        return null
    }
}
