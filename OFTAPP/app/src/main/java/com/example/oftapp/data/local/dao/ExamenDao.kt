package com.example.oftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.oftapp.data.local.entity.ExamenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamenDao {
    @Query("SELECT * FROM examenes ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE estado = 'Pendiente' ORDER BY id DESC")
    fun obtenerPendientes(): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE pacienteId = :pacienteId ORDER BY id DESC")
    fun obtenerPorPacienteId(pacienteId: Long): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE pacienteNombre LIKE '%' || :nombre || '%' ORDER BY id DESC")
    fun obtenerPorPacienteNombre(nombre: String): Flow<List<ExamenEntity>>

    @Query("SELECT * FROM examenes WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ExamenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(examen: ExamenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(examenes: List<ExamenEntity>)

    @Update
    suspend fun actualizar(examen: ExamenEntity)
}
