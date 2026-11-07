package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("idNumber") val idNumber: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("roles") val roles: List<String> = emptyList()
)

data class CreateUserDto(
    val userName: String,
    val email: String,
    val idNumber: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val roleName: String
)

data class UpdateUserDto(
    val userName: String,
    val email: String,
    val idNumber: String,
    val firstName: String,
    val lastName: String,
    val roleName: String,
    val isActive: Boolean
)

data class ResetUserPasswordDto(val userId: Int, val newPassword: String)
