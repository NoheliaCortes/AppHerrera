package com.sistemas.herrerasystemmobile.data.repository

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.LoginRequestDto

data class AuthenticatedUser(val token: String, val displayName: String, val roleId: Int?)

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(username: String, password: String): Result<AuthenticatedUser> = try {
        val response = apiService.login(LoginRequestDto(username.trim(), password))
        val body = response.body()
        val data = body?.data
        if (response.isSuccessful && body?.success == true && !data?.token.isNullOrBlank()) {
            val displayName = data?.fullName?.takeIf(String::isNotBlank)
                ?: data?.username?.takeIf(String::isNotBlank)
                ?: username.trim()
            val roleId = data!!.roles.firstNotNullOfOrNull { role ->
                role.toIntOrNull() ?: when {
                    role.contains("admin", ignoreCase = true) -> 1
                    role.contains("vendedor", ignoreCase = true) || role.contains("seller", ignoreCase = true) -> 2
                    else -> null
                }
            }
            Result.success(AuthenticatedUser(data.token!!, displayName, roleId))
        } else {
            val message = body?.message?.takeIf(String::isNotBlank)
                ?: when (response.code()) {
                    401 -> "Usuario o contraseña incorrectos"
                    403 -> "No tienes permiso para acceder"
                    else -> "No se pudo iniciar sesión (HTTP ${response.code()})"
                }
            Result.failure(IllegalStateException(message))
        }
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}
