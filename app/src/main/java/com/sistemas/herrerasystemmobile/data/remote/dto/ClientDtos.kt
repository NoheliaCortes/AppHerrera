package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ClientDto(
    @SerializedName("id") val id: Int,
    @SerializedName("municipalityId") val municipalityId: Int,
    @SerializedName("municipalityName") val municipalityName: String,
    @SerializedName("departmentId") val departmentId: Int,
    @SerializedName("departmentName") val departmentName: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("pointOfSale") val pointOfSale: String?,
    @SerializedName("posaddress") val posaddress: String?,
    @SerializedName("isActive") val isActive: Boolean?
)

data class ClientCreateDto(val municipalityId: Int, val firstName: String, val lastName: String,
    val phone: String? = null, val pointOfSale: String? = null, val posaddress: String? = null)

data class ClientUpdateDto(val municipalityId: Int, val firstName: String, val lastName: String,
    val phone: String? = null, val pointOfSale: String? = null, val posaddress: String? = null, val isActive: Boolean? = true)

data class ClientStatsDto(val activeCustomers: Int, val inactiveCustomers: Int,
    val distinctMunicipalitiesWithCustomers: Int, val activePointsOfSale: Int)
