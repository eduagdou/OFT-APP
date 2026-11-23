package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.local.entity.PacienteEntity
import com.example.oftapp.data.local.entity.UsuarioEntity
import com.example.oftapp.data.repository.AppRepository
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val error: String? = null
)

data class RegistroState(
    val rut: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val edad: String = "30",
    val correo: String = "",
    val clave: String = "",
    val rol: String = "PACIENTE",
    val sucursal: String = "Sucursal Providencia",
    val error: String? = null,
    val exitoMensaje: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        db.pacienteDao(), db.usuarioDao(), db.examenDao(), db.documentoDao(), db.atencionDao()
    )

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios.asStateFlow()

    private val _usuarioLogueado = MutableStateFlow(
        Usuario(
            id = 0,
            nombre = "Usuario no autenticado",
            correo = "",
            clave = "",
            rol = Rol.PACIENTE,
            sucursalDefault = ""
        )
    )
    val usuarioLogueado: StateFlow<Usuario> = _usuarioLogueado.asStateFlow()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _registroState = MutableStateFlow(RegistroState())
    val registroState: StateFlow<RegistroState> = _registroState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.obtenerUsuarios().collect { entities ->
                val usuarios = entities.map { entity ->
                    Usuario(
                        id = entity.id.toInt(),
                        nombre = "${entity.nombre} ${entity.apellidos}".trim(),
                        correo = entity.correo,
                        clave = "",
                        rol = parseRol(entity.rol),
                        sucursalDefault = entity.sucursal,
                        fotoPerfilUri = entity.fotoPerfilUri
                    )
                }
                _listaUsuarios.value = usuarios
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

    fun seleccionarUsuarioAuto(u: Usuario) {
        _uiState.update { it.copy(correo = u.correo, clave = u.clave, error = null) }
    }

    fun onRegRutChange(v: String) = _registroState.update { it.copy(rut = v, error = null) }
    fun onRegNombreChange(v: String) = _registroState.update { it.copy(nombre = v, error = null) }
    fun onRegApellidosChange(v: String) = _registroState.update { it.copy(apellidos = v, error = null) }
    fun onRegEdadChange(v: String) = _registroState.update { it.copy(edad = v, error = null) }
    fun onRegCorreoChange(v: String) = _registroState.update { it.copy(correo = v, error = null) }
    fun onRegClaveChange(v: String) = _registroState.update { it.copy(clave = v, error = null) }
    fun onRegRolChange(v: String) = _registroState.update { it.copy(rol = v, error = null) }
    fun onRegSucursalChange(v: String) = _registroState.update { it.copy(sucursal = v, error = null) }

    fun registrarUsuario(): Boolean {
        val r = _registroState.value
        if (r.rut.isBlank()) {
            _registroState.update { it.copy(error = "El RUT es obligatorio") }
            return false
        }
        if (r.nombre.isBlank() || r.apellidos.isBlank()) {
            _registroState.update { it.copy(error = "El nombre y apellidos son obligatorios") }
            return false
        }
        if (r.correo.isBlank() || !r.correo.contains("@")) {
            _registroState.update { it.copy(error = "Ingrese un correo electrónico válido") }
            return false
        }
        if (r.clave.length < 4) {
            _registroState.update { it.copy(error = "La contraseña debe tener al menos 4 caracteres") }
            return false
        }

        viewModelScope.launch {
            val nuevaEntidadUsuario = UsuarioEntity(
                rut = r.rut.trim(),
                nombre = r.nombre.trim(),
                apellidos = r.apellidos.trim(),
                edad = r.edad.toIntOrNull() ?: 30,
                telefono = "+56 9 1234 5678",
                correo = r.correo.trim().lowercase(),
                clave = r.clave.trim(),
                rol = r.rol,
                sucursal = r.sucursal
            )

            val cuentaInsforge = repository.signUp(
                r.correo.trim().lowercase(),
                r.clave.trim(),
                "${r.nombre.trim()} ${r.apellidos.trim()}".trim()
            )

            repository.insertarUsuario(nuevaEntidadUsuario)

            if (r.rol == "PACIENTE") {
                val nuevaEntidadPaciente = PacienteEntity(
                    rut = r.rut.trim(),
                    nombre = r.nombre.trim(),
                    apellidos = r.apellidos.trim(),
                    edad = r.edad.toIntOrNull() ?: 30,
                    prevision = "Fonasa",
                    telefono = "+56 9 1234 5678",
                    correo = r.correo.trim().lowercase(),
                    clave = r.clave.trim()
                )
                repository.insertarPaciente(nuevaEntidadPaciente)
            }

            val nuevoUsuario = Usuario(
                id = (System.currentTimeMillis() % 100000).toInt(),
                nombre = "${r.nombre.trim()} ${r.apellidos.trim()}",
                correo = r.correo.trim().lowercase(),
                clave = r.clave.trim(),
                rol = parseRol(r.rol),
                sucursalDefault = r.sucursal
            )

            _listaUsuarios.update { it + nuevoUsuario }
            _registroState.update {
                it.copy(
                    error = if (cuentaInsforge == null) "La cuenta local fue creada, pero InsForge rechazó el registro. Verifica correo y contraseña." else null,
                    exitoMensaje = if (cuentaInsforge == null) null else "¡Cuenta creada y conectada a InsForge! Revisa tu correo si se requiere verificación."
                )
            }
            _uiState.update { it.copy(correo = r.correo.trim().lowercase(), clave = r.clave.trim()) }
        }
        return true
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val s = _uiState.value
        val correoClean = s.correo.trim().lowercase()
        val claveClean = s.clave.trim()

        if (correoClean.isBlank() || claveClean.isBlank()) {
            _uiState.update { it.copy(error = "Debe ingresar correo y contraseña.") }
            return
        }

        viewModelScope.launch {
            val sesionInsforge = repository.signIn(correoClean, claveClean)
            if (sesionInsforge != null) {
                val usuarioDb = repository.loginUsuario(correoClean, claveClean)
                val usuarioRemoto = usuarioDb?.let {
                    Usuario(
                        id = it.id.toInt(),
                        nombre = "${it.nombre} ${it.apellidos}".trim(),
                        correo = it.correo,
                        clave = it.clave,
                        rol = parseRol(it.rol),
                        sucursalDefault = it.sucursal,
                        fotoPerfilUri = it.fotoPerfilUri
                    )
                } ?: Usuario(
                    id = 0,
                    nombre = sesionInsforge.user.email,
                    correo = sesionInsforge.user.email,
                    clave = "",
                    rol = when (correoClean) {
                        "medico@oftapp.demo" -> Rol.MEDICO_OFTALMOLOGO
                        "tecnologo@oftapp.demo" -> Rol.TECNOLOGO_MEDICO
                        "administrativo@oftapp.demo" -> Rol.PERSONAL_ADMINISTRATIVO
                        "admin@oftapp.demo" -> Rol.ADMINISTRADOR
                        else -> Rol.PACIENTE
                    },
                    sucursalDefault = "Sucursal asignada"
                )

                _usuarioLogueado.value = usuarioRemoto
                _uiState.update { it.copy(error = null) }
                repository.sincronizarConInsforge()
                onExito()
                return@launch
            }

            val usuarioLocal = repository.loginUsuario(correoClean, claveClean)
            if (usuarioLocal != null) {
                _usuarioLogueado.value = Usuario(
                    id = usuarioLocal.id.toInt(),
                    nombre = "${usuarioLocal.nombre} ${usuarioLocal.apellidos}".trim(),
                    correo = usuarioLocal.correo,
                    clave = usuarioLocal.clave,
                    rol = parseRol(usuarioLocal.rol),
                    sucursalDefault = usuarioLocal.sucursal,
                    fotoPerfilUri = usuarioLocal.fotoPerfilUri
                )
                _uiState.update { it.copy(error = null) }
                onExito()
                return@launch
            }

            _usuarioLogueado.value = Usuario(
                id = 0,
                nombre = "Usuario no autenticado",
                correo = "",
                clave = "",
                rol = Rol.PACIENTE,
                sucursalDefault = ""
            )
            _uiState.update { it.copy(error = "No se pudo iniciar sesión en InsForge. Verifica el correo y la contraseña.") }
        }
    }

    fun cerrarSesion() {
        val actual = _usuarioLogueado.value
        _uiState.value = LoginUiState(correo = actual.correo, clave = actual.clave)
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
            return "La contraseña actual no coincide con la sesión local."
        }
        if (nuevaClave.trim().length < 4) {
            return "La nueva contraseña debe tener al menos 4 caracteres."
        }
        if (nuevaClave == claveActual) {
            return "La nueva contraseña debe ser diferente a la contraseña actual."
        }

        viewModelScope.launch {
            val enviado = repository.solicitarRestablecimientoCorreo(usuarioActual.correo)
            if (!enviado) {
                _uiState.update { it.copy(error = "No se pudo enviar el restablecimiento de contraseña.") }
            }
        }

        return null
    }
}
