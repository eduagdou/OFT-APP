package com.example.oftapp.data.repository

import com.example.oftapp.data.remote.FhirApiService
import com.example.oftapp.data.remote.FhirObservation
import com.example.oftapp.data.remote.FhirPatient

class FhirRepository(private val apiService: FhirApiService = FhirApiService.create()) {

    suspend fun consultarPacientesDemo(): List<FhirPatient> {
        return try {
            val response = apiService.getPatients(count = 5)
            if (response.isSuccessful) {
                response.body()?.entry?.mapNotNull { it.resource } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun consultarObservacionesClinicas(): List<FhirObservation> {
        return try {
            val response = apiService.getObservations(count = 5)
            if (response.isSuccessful) {
                response.body()?.entry?.mapNotNull { it.resource } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
