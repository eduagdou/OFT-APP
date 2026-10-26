package com.example.oftapp.data.repository

import com.example.oftapp.data.local.dao.ExamenDao
import com.example.oftapp.data.local.dao.PacienteDao
import com.example.oftapp.data.local.dao.UsuarioDao
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.local.entity.PacienteEntity
import com.example.oftapp.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val pacienteDao: PacienteDao,
    private val usuarioDao: UsuarioDao,
    private val examenDao: ExamenDao
) {
    // Pacientes
    fun obtenerPacientes(): Flow<List<PacienteEntity>> = pacienteDao.obtenerTodos()
    suspend fun obtenerPacientePorId(id: Long): PacienteEntity? = pacienteDao.obtenerPorId(id)
    suspend fun loginPaciente(correo: String, clave: String): PacienteEntity? = pacienteDao.login(correo, clave)
    suspend fun insertarPaciente(paciente: PacienteEntity): Long = pacienteDao.insertar(paciente)
    suspend fun actualizarPaciente(paciente: PacienteEntity) = pacienteDao.actualizar(paciente)

    // Usuarios (Médicos, Personal, Admin)
    fun obtenerUsuarios(): Flow<List<UsuarioEntity>> = usuarioDao.obtenerTodos()
    fun obtenerMedicos(): Flow<List<UsuarioEntity>> = usuarioDao.obtenerMedicos()
    suspend fun obtenerUsuarioPorId(id: Long): UsuarioEntity? = usuarioDao.obtenerPorId(id)
    suspend fun loginUsuario(correo: String, clave: String): UsuarioEntity? = usuarioDao.login(correo, clave)
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long = usuarioDao.insertar(usuario)
    suspend fun actualizarUsuario(usuario: UsuarioEntity) = usuarioDao.actualizar(usuario)

    // Exámenes
    fun obtenerExamenes(): Flow<List<ExamenEntity>> = examenDao.obtenerTodos()
    fun obtenerExamenesPendientes(): Flow<List<ExamenEntity>> = examenDao.obtenerPendientes()
    fun obtenerExamenesPorPacienteId(pacienteId: Long): Flow<List<ExamenEntity>> = examenDao.obtenerPorPacienteId(pacienteId)
    fun obtenerExamenesPorPacienteNombre(nombre: String): Flow<List<ExamenEntity>> = examenDao.obtenerPorPacienteNombre(nombre)
    suspend fun obtenerExamenPorId(id: Long): ExamenEntity? = examenDao.obtenerPorId(id)
    suspend fun insertarExamen(examen: ExamenEntity): Long = examenDao.insertar(examen)
    suspend fun actualizarExamen(examen: ExamenEntity) = examenDao.actualizar(examen)
}
