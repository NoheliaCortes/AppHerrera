package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

/** API envelope used by the existing Herrera endpoints. */
data class LoginResponseDto(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: LoginDataDto? = null
)

data class LoginDataDto(
    @SerializedName(value = "token", alternate = ["accessToken"]) val token: String? = null,
    @SerializedName(value = "username", alternate = ["userName"]) val username: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("roles") val roles: List<String> = emptyList()
)
