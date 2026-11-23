package com.example.oftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oftapp.data.local.entity.DocumentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {
    @Query("SELECT * FROM documentos_examen WHERE examenId = :examenId ORDER BY id DESC")
    fun obtenerPorExamenId(examenId: Long): Flow<List<DocumentoEntity>>

    @Query("SELECT * FROM documentos_examen ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<DocumentoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(documento: DocumentoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(documentos: List<DocumentoEntity>)
}
