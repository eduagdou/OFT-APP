package com.example.oftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oftapp.data.local.entity.AtencionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AtencionDao {
    @Query("SELECT * FROM atenciones ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<AtencionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(atencion: AtencionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(atenciones: List<AtencionEntity>)
}
