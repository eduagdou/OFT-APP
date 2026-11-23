package com.example.oftapp.data.remote

import com.google.gson.JsonObject
import com.example.oftapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

object InsforgeSession {
    @Volatile var accessToken: String? = null
    @Volatile var refreshToken: String? = null
}

data class InsforgeAuthUser(
    val id: String,
    val email: String,
    val emailVerified: Boolean = false,
    val role: String? = null
)

data class InsforgeSessionResponse(
    val user: InsforgeAuthUser,
    val accessToken: String,
    val refreshToken: String? = null,
    val requireEmailVerification: Boolean = false
)

data class InsforgeSignInRequest(
    val method: String = "password",
    val email: String,
    val password: String
)

data class InsforgeSignUpRequest(
    val email: String,
    val password: String,
    val name: String
)

data class InsforgeResetRequest(
    val newPassword: String,
    val otp: String
)

interface InsforgeApiService {
    @GET("api/database/records/pacientes")
    suspend fun getPacientes(@Query("limit") limit: Int = 1000): Response<List<JsonObject>>

    @POST("api/database/records/pacientes")
    suspend fun crearPaciente(@Body paciente: JsonObject): Response<List<JsonObject>>

    @GET("api/database/records/usuarios")
    suspend fun getUsuarios(@Query("limit") limit: Int = 1000): Response<List<JsonObject>>

    @POST("api/database/records/usuarios")
    suspend fun crearUsuario(@Body usuario: JsonObject): Response<List<JsonObject>>

    @GET("api/database/records/atenciones")
    suspend fun getAtenciones(@Query("limit") limit: Int = 1000): Response<List<JsonObject>>

    @POST("api/database/records/atenciones")
    suspend fun crearAtencion(@Body atencion: JsonObject): Response<List<JsonObject>>

    @GET("api/database/records/examenes")
    suspend fun getExamenes(@Query("limit") limit: Int = 1000): Response<List<JsonObject>>

    @POST("api/database/records/examenes")
    suspend fun crearExamen(@Body examen: JsonObject): Response<List<JsonObject>>

    @retrofit2.http.PATCH("api/database/records/examenes")
    suspend fun actualizarExamen(
        @Query("id") idFilter: String,
        @Body examen: JsonObject
    ): Response<List<JsonObject>>

    @GET("api/database/records/documentos_examen")
    suspend fun getDocumentos(@Query("limit") limit: Int = 1000): Response<List<JsonObject>>

    @POST("api/database/records/documentos_examen")
    suspend fun crearDocumento(@Body documento: JsonObject): Response<List<JsonObject>>

    @POST("api/database/records/sync_log")
    suspend fun crearSyncLog(@Body registro: JsonObject): Response<List<JsonObject>>

    @GET("api/insforge/roles")
    suspend fun getRoles(@Query("limit") limit: Int = 100): Response<List<JsonObject>>

    @GET("api/insforge/sucursales")
    suspend fun getSucursales(@Query("limit") limit: Int = 100): Response<List<JsonObject>>

    @POST
    suspend fun signUpDirect(@Url url: String, @Body request: InsforgeSignUpRequest): Response<InsforgeSessionResponse>

    @POST
    suspend fun signInDirect(@Url url: String, @Body request: InsforgeSignInRequest): Response<InsforgeSessionResponse>

    @POST
    suspend fun logoutDirect(@Url url: String): Response<JsonObject>

    @POST("api/auth/email/send-reset-password")
    suspend fun enviarResetPassword(@Body request: JsonObject): Response<JsonObject>

    companion object {
        fun create(): InsforgeApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val authInterceptor = Interceptor { chain ->
                val token = InsforgeSession.accessToken
                    ?: BuildConfig.INSFORGE_ANON_KEY
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://iyek7p9n.us-east.insforge.app/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(InsforgeApiService::class.java)
        }
    }
}
