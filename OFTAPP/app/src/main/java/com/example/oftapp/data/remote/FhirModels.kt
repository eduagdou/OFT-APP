package com.example.oftapp.data.remote

import com.google.gson.annotations.SerializedName

data class FhirBundleResponse<T>(
    @SerializedName("resourceType") val resourceType: String,
    @SerializedName("total") val total: Int? = 0,
    @SerializedName("entry") val entry: List<FhirEntry<T>>? = emptyList()
)

data class FhirEntry<T>(
    @SerializedName("resource") val resource: T
)

data class FhirPatient(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: List<FhirName>? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("birthDate") val birthDate: String? = null
)

data class FhirName(
    @SerializedName("family") val family: String? = null,
    @SerializedName("given") val given: List<String>? = null
)

data class FhirObservation(
    @SerializedName("id") val id: String?,
    @SerializedName("status") val status: String? = null,
    @SerializedName("code") val code: FhirCodeableConcept? = null,
    @SerializedName("subject") val subject: FhirReference? = null,
    @SerializedName("valueString") val valueString: String? = null
)

data class FhirCodeableConcept(
    @SerializedName("text") val text: String? = null,
    @SerializedName("coding") val coding: List<FhirCoding>? = null
)

data class FhirCoding(
    @SerializedName("system") val system: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("display") val display: String? = null
)

data class FhirReference(
    @SerializedName("reference") val reference: String? = null,
    @SerializedName("display") val display: String? = null
)
