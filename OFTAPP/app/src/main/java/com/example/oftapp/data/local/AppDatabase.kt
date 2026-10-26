package com.example.oftapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.oftapp.data.local.dao.ExamenDao
import com.example.oftapp.data.local.dao.PacienteDao
import com.example.oftapp.data.local.dao.UsuarioDao
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.local.entity.PacienteEntity
import com.example.oftapp.data.local.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PacienteEntity::class, UsuarioEntity::class, ExamenEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pacienteDao(): PacienteDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun examenDao(): ExamenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oftapp_database"
                )
                    .addCallback(AppDatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        poblarBaseDatos(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (database.usuarioDao().obtenerPorId(1) == null) {
                            poblarBaseDatos(database)
                        }
                    }
                }
            }
        }

        private suspend fun poblarBaseDatos(db: AppDatabase) {
            // Pacientes demo iniciales
            val pacientes = listOf(
                PacienteEntity(
                    id = 1,
                    rut = "18.432.109-K",
                    nombre = "Camila",
                    apellidos = "Rojas",
                    edad = 34,
                    prevision = "Fonasa",
                    telefono = "+56 9 8765 4321",
                    correo = "paciente001@pacientes.com",
                    clave = "1234"
                ),
                PacienteEntity(
                    id = 2,
                    rut = "12.876.543-2",
                    nombre = "Jorge",
                    apellidos = "Valenzuela",
                    edad = 52,
                    prevision = "Isapre",
                    telefono = "+56 9 7654 3210",
                    correo = "jorge.valenzuela@pacientes.com",
                    clave = "1234"
                ),
                PacienteEntity(
                    id = 3,
                    rut = "9.876.543-1",
                    nombre = "María Teresa",
                    apellidos = "Soto",
                    edad = 68,
                    prevision = "Fonasa",
                    telefono = "+56 9 6543 2109",
                    correo = "mteresa.soto@pacientes.com",
                    clave = "1234"
                )
            )
            db.pacienteDao().insertarTodos(pacientes)

            // Usuarios / Personal demo iniciales
            val usuarios = listOf(
                UsuarioEntity(
                    id = 1,
                    rut = "18.432.109-K",
                    nombre = "Camila",
                    apellidos = "Rojas",
                    edad = 34,
                    telefono = "+56 9 8765 4321",
                    correo = "paciente001@pacientes.com",
                    clave = "1234",
                    rol = "PACIENTE",
                    sucursal = "Sucursal Providencia"
                ),
                UsuarioEntity(
                    id = 2,
                    rut = "15.111.222-3",
                    nombre = "Dr. Ricardo",
                    apellidos = "Sepúlveda",
                    edad = 45,
                    telefono = "+56 9 1111 2222",
                    correo = "medico001@empleados.com",
                    clave = "1234",
                    rol = "MEDICO_OFTALMOLOGO",
                    sucursal = "Sucursal Central"
                ),
                UsuarioEntity(
                    id = 3,
                    rut = "16.333.444-5",
                    nombre = "TM. Andrea",
                    apellidos = "Morales",
                    edad = 38,
                    telefono = "+56 9 3333 4444",
                    correo = "tecmed001@empleados.com",
                    clave = "1234",
                    rol = "TECNOLOGO_MEDICO",
                    sucursal = "Sucursal Providencia"
                ),
                UsuarioEntity(
                    id = 4,
                    rut = "17.555.666-7",
                    nombre = "Carlos",
                    apellidos = "Fuentes",
                    edad = 29,
                    telefono = "+56 9 5555 6666",
                    correo = "adminsec001@empleados.com",
                    clave = "1234",
                    rol = "PERSONAL_ADMINISTRATIVO",
                    sucursal = "Sucursal Central"
                ),
                UsuarioEntity(
                    id = 5,
                    rut = "10.777.888-9",
                    nombre = "Prof. Manuel",
                    apellidos = "Silva",
                    edad = 55,
                    telefono = "+56 9 7777 8888",
                    correo = "admin001@admin.com",
                    clave = "1234",
                    rol = "ADMINISTRADOR",
                    sucursal = "Todas las sucursales"
                )
            )
            db.usuarioDao().insertarTodos(usuarios)

            // Exámenes demo iniciales (Pendientes y Realizados)
            val examenes = listOf(
                ExamenEntity(
                    id = 1,
                    pacienteId = 1,
                    pacienteNombre = "Camila Rojas",
                    medicoId = 3,
                    medicoNombre = "TM. Andrea Morales",
                    tipoExamen = "Agudeza visual",
                    ojoEvaluado = "Ambos Ojos (OU)",
                    fechaExamen = "02/09/2026",
                    fechaResolucion = "02/09/2026",
                    estado = "Realizado",
                    comentarios = "OD: 20/25 · OI: 20/20. Visión binocular conservada.",
                    sucursal = "Sucursal Providencia"
                ),
                ExamenEntity(
                    id = 2,
                    pacienteId = 1,
                    pacienteNombre = "Camila Rojas",
                    medicoId = 3,
                    medicoNombre = "TM. Andrea Morales",
                    tipoExamen = "Presión intraocular",
                    ojoEvaluado = "Ambos Ojos (OU)",
                    fechaExamen = "02/09/2026",
                    fechaResolucion = "02/09/2026",
                    estado = "Realizado",
                    comentarios = "OD: 14 mmHg · OI: 15 mmHg. Presión dentro de rango normal.",
                    sucursal = "Sucursal Providencia"
                ),
                ExamenEntity(
                    id = 3,
                    pacienteId = 1,
                    pacienteNombre = "Camila Rojas",
                    medicoId = 2,
                    medicoNombre = "Dr. Ricardo Sepúlveda",
                    tipoExamen = "Fondo de ojo",
                    ojoEvaluado = "Ambos Ojos (OU)",
                    fechaExamen = "20/10/2026",
                    fechaResolucion = null,
                    estado = "Pendiente",
                    comentarios = "Evaluación de retina bajo dilatación pupilar requerida.",
                    sucursal = "Sucursal Providencia"
                ),
                ExamenEntity(
                    id = 4,
                    pacienteId = 2,
                    pacienteNombre = "Jorge Valenzuela",
                    medicoId = 3,
                    medicoNombre = "TM. Andrea Morales",
                    tipoExamen = "Campo visual",
                    ojoEvaluado = "Ojo Izquierdo (OI)",
                    fechaExamen = "05/11/2026",
                    fechaResolucion = null,
                    estado = "Pendiente",
                    comentarios = "Campimetría computarizada para descarte de glaucoma.",
                    sucursal = "Sucursal Central"
                )
            )
            db.examenDao().insertarTodos(examenes)
        }
    }
}
