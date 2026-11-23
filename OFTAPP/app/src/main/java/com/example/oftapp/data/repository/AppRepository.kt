package com.example.oftapp.data.repository

import com.example.oftapp.data.local.dao.AtencionDao
import com.example.oftapp.data.local.dao.DocumentoDao
import com.example.oftapp.data.local.dao.ExamenDao
import com.example.oftapp.data.local.dao.PacienteDao
import com.example.oftapp.data.local.dao.UsuarioDao
import com.example.oftapp.data.local.entity.AtencionEntity
import com.example.oftapp.data.local.entity.DocumentoEntity
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.local.entity.PacienteEntity
import com.example.oftapp.data.local.entity.UsuarioEntity
import com.example.oftapp.data.remote.InsforgeApiService
import com.example.oftapp.data.remote.InsforgeSignInRequest
import com.example.oftapp.data.remote.InsforgeSignUpRequest
import com.example.oftapp.data.remote.InsforgeSession
import com.example.oftapp.data.remote.InsforgeSessionResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val pacienteDao: PacienteDao,
    private val usuarioDao: UsuarioDao,
    private val examenDao: ExamenDao,
    private val documentoDao: DocumentoDao? = null,
    private val atencionDao: AtencionDao? = null,
    private val insforgeApi: InsforgeApiService = InsforgeApiService.create()
) {
    private fun longValue(row: JsonObject, key: String, default: Long = 0): Long =
        row.get(key)?.takeUnless { it.isJsonNull }?.asLong ?: default

    private fun intValue(row: JsonObject, key: String, default: Int = 0): Int =
        row.get(key)?.takeUnless { it.isJsonNull }?.asInt ?: default

    private fun stringValue(row: JsonObject, key: String, default: String = ""): String =
        row.get(key)?.takeUnless { it.isJsonNull }?.asString ?: default

    private fun nullableString(row: JsonObject, key: String): String? =
        row.get(key)?.takeUnless { it.isJsonNull }?.asString

    private fun pacienteFromRow(row: JsonObject) = PacienteEntity(
        id = longValue(row, "id"),
        rut = stringValue(row, "rut"),
        nombre = stringValue(row, "nombre"),
        apellidos = stringValue(row, "apellidos"),
        edad = intValue(row, "edad"),
        prevision = stringValue(row, "prevision"),
        telefono = nullableString(row, "telefono") ?: "",
        correo = stringValue(row, "correo"),
        clave = ""
    )

    private fun usuarioFromRow(row: JsonObject) = UsuarioEntity(
        id = longValue(row, "id"),
        rut = stringValue(row, "rut"),
        nombre = stringValue(row, "nombre"),
        apellidos = stringValue(row, "apellidos"),
        edad = row.get("edad")?.takeUnless { it.isJsonNull }?.asInt,
        telefono = nullableString(row, "telefono") ?: "",
        correo = stringValue(row, "correo"),
        clave = "",
        rol = when (intValue(row, "rol_id", -1)) {
            1 -> "PACIENTE"
            2 -> "MEDICO_OFTALMOLOGO"
            3 -> "TECNOLOGO_MEDICO"
            4 -> "PERSONAL_ADMINISTRATIVO"
            5 -> "ADMINISTRADOR"
            else -> "PACIENTE"
        },
        sucursal = when (intValue(row, "sucursal_id", -1)) {
            1 -> "Sucursal Providencia"
            2 -> "Sucursal Central"
            3 -> "Sucursal Las Condes"
            else -> ""
        },
        fotoPerfilUri = nullableString(row, "foto_perfil_uri")
    )

    private fun atencionFromRow(row: JsonObject) = AtencionEntity(
        id = longValue(row, "id"),
        pacienteId = longValue(row, "paciente_id", 1),
        pacienteNombre = "Paciente ${longValue(row, "paciente_id", 1)}",
        medicoId = row.get("medico_id")?.takeUnless { it.isJsonNull }?.asLong,
        medicoNombre = null,
        sucursalId = row.get("sucursal_id")?.takeUnless { it.isJsonNull }?.asLong,
        sucursal = when (row.get("sucursal_id")?.takeUnless { it.isJsonNull }?.asInt) {
            1 -> "Sucursal Providencia"
            2 -> "Sucursal Central"
            3 -> "Sucursal Las Condes"
            else -> ""
        },
        fecha = stringValue(row, "fecha"),
        motivo = stringValue(row, "motivo"),
        estado = stringValue(row, "estado", "Programada")
    )

    private fun examenFromRow(row: JsonObject) = ExamenEntity(
        id = longValue(row, "id"),
        pacienteId = longValue(row, "paciente_id"),
        pacienteNombre = "Paciente ${longValue(row, "paciente_id")}",
        medicoId = row.get("medico_id")?.takeUnless { it.isJsonNull }?.asLong,
        medicoNombre = null,
        tipoExamen = stringValue(row, "tipo_examen"),
        ojoEvaluado = stringValue(row, "ojo_evaluado"),
        fechaExamen = stringValue(row, "fecha_examen"),
        fechaResolucion = nullableString(row, "fecha_resolucion"),
        estado = stringValue(row, "estado", "Pendiente"),
        comentarios = nullableString(row, "descripcion"),
        sucursal = stringValue(row, "sucursal"),
        archivosAdjuntos = null,
        sincronizado = true
    )

    private fun documentoFromRow(row: JsonObject) = DocumentoEntity(
        id = longValue(row, "id"),
        examenId = longValue(row, "examen_id"),
        nombreArchivo = stringValue(row, "nombre_archivo"),
        tipoArchivo = stringValue(row, "tipo_archivo"),
        tamanio = stringValue(row, "tamanio"),
        fechaCarga = stringValue(row, "fecha_carga"),
        contenidoSimulado = nullableString(row, "contenido_simulado"),
        uriPath = nullableString(row, "uri_path")
    )

    private fun jsonRow(obj: Any): JsonObject = com.google.gson.Gson().toJsonTree(obj).asJsonObject

    suspend fun signIn(email: String, password: String): InsforgeSessionResponse? =
        try {
            val response = insforgeApi.signInDirect(
                "https://iyek7p9n.us-east.insforge.app/api/auth/sessions?client_type=mobile",
                InsforgeSignInRequest(email = email, password = password)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                InsforgeSession.accessToken = body.accessToken
                InsforgeSession.refreshToken = body.refreshToken
                body
            } else null
        } catch (_: Exception) { null }

    suspend fun signUp(email: String, password: String, name: String): InsforgeSessionResponse? =
        try {
            val response = insforgeApi.signUpDirect(
                "https://iyek7p9n.us-east.insforge.app/api/auth/users?client_type=mobile",
                InsforgeSignUpRequest(email, password, name)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                InsforgeSession.accessToken = body.accessToken
                InsforgeSession.refreshToken = body.refreshToken
                body
            } else null
        } catch (_: Exception) { null }

    suspend fun cerrarSesionInsforge() {
        try {
            insforgeApi.logoutDirect("https://iyek7p9n.us-east.insforge.app/api/auth/logout")
        } catch (_: Exception) {}
        InsforgeSession.accessToken = null
        InsforgeSession.refreshToken = null
    }

    suspend fun sincronizarConInsforge(): Boolean {
        val exito = try {
            val pacientes = insforgeApi.getPacientes()
            if (pacientes.isSuccessful) pacientes.body()?.let { rows -> pacienteDao.insertarTodos(rows.map(::pacienteFromRow)) }

            val usuarios = insforgeApi.getUsuarios()
            if (usuarios.isSuccessful) usuarios.body()?.let { rows -> usuarioDao.insertarTodos(rows.map(::usuarioFromRow)) }

            val examenes = insforgeApi.getExamenes()
            if (examenes.isSuccessful) examenes.body()?.let { rows -> examenDao.insertarTodos(rows.map(::examenFromRow)) }

            val atenciones = insforgeApi.getAtenciones()
            if (atenciones.isSuccessful) atenciones.body()?.let { rows -> atencionDao?.insertarTodos(rows.map(::atencionFromRow)) }

            val documentos = insforgeApi.getDocumentos()
            if (documentos.isSuccessful) documentos.body()?.let { rows -> documentoDao?.insertarTodos(rows.map(::documentoFromRow)) }

            pacientes.isSuccessful && usuarios.isSuccessful && examenes.isSuccessful &&
                atenciones.isSuccessful && documentos.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

        val registro = JsonObject().apply {
            addProperty("tabla_afectada", "sincronizacion")
            addProperty("registro_id", 0)
            addProperty("operacion", if (exito) "SYNC_EXITOSO" else "SYNC_ERROR")
            addProperty("timestamp", System.currentTimeMillis())
            addProperty("sincronizado", if (exito) 1 else 0)
        }

        try {
            insforgeApi.crearSyncLog(registro)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return exito
    }

    // Pacientes
    fun obtenerPacientes(): Flow<List<PacienteEntity>> = pacienteDao.obtenerTodos()
    suspend fun obtenerPacientePorId(id: Long): PacienteEntity? = pacienteDao.obtenerPorId(id)
    suspend fun loginPaciente(correo: String, clave: String): PacienteEntity? = pacienteDao.login(correo, clave)

    suspend fun insertarPaciente(paciente: PacienteEntity): Long {
        val id = pacienteDao.insertar(paciente)
        val remote = jsonRow(paciente)
        remote.remove("clave")
        remote.remove("antecedentes")
        remote.remove("id")
        try { insforgeApi.crearPaciente(remote) } catch (_: Exception) {}
        return id
    }

    suspend fun actualizarPaciente(paciente: PacienteEntity) = pacienteDao.actualizar(paciente)

    // Usuarios (Médicos, Personal, Admin)
    fun obtenerUsuarios(): Flow<List<UsuarioEntity>> = usuarioDao.obtenerTodos()
    fun obtenerMedicos(): Flow<List<UsuarioEntity>> = usuarioDao.obtenerMedicos()
    suspend fun obtenerUsuarioPorId(id: Long): UsuarioEntity? = usuarioDao.obtenerPorId(id)
    suspend fun loginUsuario(correo: String, clave: String): UsuarioEntity? = usuarioDao.login(correo, clave)

    suspend fun insertarUsuario(usuario: UsuarioEntity): Long {
        val id = usuarioDao.insertar(usuario)
        val remote = JsonObject()
        remote.addProperty("rut", usuario.rut)
        remote.addProperty("nombre", usuario.nombre)
        remote.addProperty("apellidos", usuario.apellidos)
        remote.addProperty("edad", usuario.edad)
        remote.addProperty("telefono", usuario.telefono)
        remote.addProperty("correo", usuario.correo)
        remote.addProperty("clave", usuario.clave)
        remote.addProperty("rol_id", when (usuario.rol) { "PACIENTE" -> 1; "MEDICO_OFTALMOLOGO" -> 2; "TECNOLOGO_MEDICO" -> 3; "PERSONAL_ADMINISTRATIVO" -> 4; "ADMINISTRADOR" -> 5; else -> 1 })
        remote.addProperty("sucursal_id", when (usuario.sucursal) { "Sucursal Providencia" -> 1; "Sucursal Central" -> 2; "Sucursal Las Condes" -> 3; else -> 1 })
        try { insforgeApi.crearUsuario(remote) } catch (_: Exception) {}
        return id
    }

    suspend fun actualizarUsuario(usuario: UsuarioEntity) = usuarioDao.actualizar(usuario)

    // Atenciones
    fun obtenerAtenciones(): Flow<List<AtencionEntity>>? = atencionDao?.obtenerTodos()

    suspend fun insertarAtencion(atencion: AtencionEntity): Long? {
        val id = atencionDao?.insertar(atencion)
        val remote = JsonObject()
        remote.addProperty("paciente_id", atencion.pacienteId)
        atencion.medicoId?.let { remote.addProperty("medico_id", it) }
        atencion.sucursalId?.let { remote.addProperty("sucursal_id", it) }
        remote.addProperty("fecha", atencion.fecha)
        remote.addProperty("motivo", atencion.motivo)
        remote.addProperty("estado", atencion.estado)
        try { insforgeApi.crearAtencion(remote) } catch (_: Exception) {}
        return id
    }

    // Exámenes
    fun obtenerExamenes(): Flow<List<ExamenEntity>> = examenDao.obtenerTodos()
    fun obtenerExamenesPendientes(): Flow<List<ExamenEntity>> = examenDao.obtenerPendientes()
    fun obtenerExamenesPorPacienteId(pacienteId: Long): Flow<List<ExamenEntity>> = examenDao.obtenerPorPacienteId(pacienteId)
    fun obtenerExamenesPorPacienteNombre(nombre: String): Flow<List<ExamenEntity>> = examenDao.obtenerPorPacienteNombre(nombre)
    suspend fun obtenerExamenPorId(id: Long): ExamenEntity? = examenDao.obtenerPorId(id)

    suspend fun insertarExamen(examen: ExamenEntity): Long {
        val id = examenDao.insertar(examen)
        val remote = JsonObject()
        remote.addProperty("paciente_id", examen.pacienteId)
        examen.medicoId?.let { remote.addProperty("medico_id", it) }
        remote.addProperty("tipo_examen", examen.tipoExamen)
        remote.addProperty("ojo_evaluado", examen.ojoEvaluado)
        remote.addProperty("fecha_examen", examen.fechaExamen)
        examen.fechaResolucion?.let { remote.addProperty("fecha_resolucion", it) }
        remote.addProperty("estado", examen.estado)
        remote.addProperty("descripcion", examen.comentarios ?: "")
        remote.addProperty("sucursal", examen.sucursal)
        try { insforgeApi.crearExamen(remote) } catch (_: Exception) {}
        return id
    }

    suspend fun actualizarExamen(examen: ExamenEntity) {
        examenDao.actualizar(examen)
        val remote = JsonObject()
        remote.addProperty("estado", examen.estado)
        examen.comentarios?.let { remote.addProperty("descripcion", it) }
        try { insforgeApi.actualizarExamen("eq.${examen.id}", remote) } catch (_: Exception) {}
    }

    // Documentos Examen
    fun obtenerDocumentosPorExamenId(examenId: Long): Flow<List<DocumentoEntity>>? = documentoDao?.obtenerPorExamenId(examenId)

    suspend fun insertarDocumento(documento: DocumentoEntity): Long? {
        val id = documentoDao?.insertar(documento)
        val remote = JsonObject()
        remote.addProperty("examen_id", documento.examenId)
        remote.addProperty("nombre_archivo", documento.nombreArchivo)
        remote.addProperty("tipo_archivo", documento.tipoArchivo)
        remote.addProperty("tamanio", documento.tamanio)
        remote.addProperty("fecha_carga", documento.fechaCarga)
        documento.contenidoSimulado?.let { remote.addProperty("contenido_simulado", it) }
        documento.uriPath?.let { remote.addProperty("uri_path", it) }
        try { insforgeApi.crearDocumento(remote) } catch (_: Exception) {}
        return id
    }

    suspend fun obtenerPacientePorRut(rut: String): PacienteEntity? {
        val rutNormalizado = rut.trim().replace(".", "").replace("-", "").uppercase()
        val response = insforgeApi.getPacientes(limit = 1000)
        if (!response.isSuccessful) return null
        return response.body()?.map(::pacienteFromRow)?.firstOrNull { remoto ->
            val remotoNormalizado = remoto.rut.replace(".", "").replace("-", "").uppercase()
            remotoNormalizado == rutNormalizado
        }
    }

    suspend fun solicitarRestablecimientoCorreo(email: String): Boolean = try {
        val body = JsonObject().apply { addProperty("email", email) }
        insforgeApi.enviarResetPassword(body).isSuccessful
    } catch (_: Exception) { false }
}
