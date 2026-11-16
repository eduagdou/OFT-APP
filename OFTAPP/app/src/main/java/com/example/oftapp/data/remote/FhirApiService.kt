package com.example.oftapp.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FhirApiService {

    @GET("Patient")
    suspend fun getPatients(
        @Query("_count") count: Int = 10
    ): Response<FhirBundleResponse<FhirPatient>>

    @GET("Observation")
    suspend fun getObservations(
        @Query("_count") count: Int = 10
    ): Response<FhirBundleResponse<FhirObservation>>

    companion object {
        private const val BASE_URL = "https://hapi.fhir.org/baseR4/"

        fun create(): FhirApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FhirApiService::class.java)
        }
    }
}
