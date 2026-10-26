package com.example.oftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.oftapp.data.local.entity.PacienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PacienteDao {
    @Query("SELECT * FROM pacientes")
    fun obtenerTodos(): Flow<List<PacienteEntity>>

    @Query("SELECT * FROM pacientes WHERE id = :id")
    suspend fun obtenerPorId(id: Long): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): PacienteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(paciente: PacienteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(pacientes: List<PacienteEntity>)

    @Update
    suspend fun actualizar(paciente: PacienteEntity)
}
