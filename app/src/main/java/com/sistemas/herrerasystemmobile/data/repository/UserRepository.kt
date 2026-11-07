package com.sistemas.herrerasystemmobile.data.repository

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.*

class UserRepository(private val api: ApiService) {
    suspend fun getUsers(): Result<List<UserDto>> = try {
        val response = api.getUsers()
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) Result.success(body.data)
        else Result.failure(Exception(body?.message ?: "Error HTTP ${response.code()} al cargar usuarios"))
    } catch (error: Exception) { Result.failure(error) }

    suspend fun create(dto: CreateUserDto): Result<Unit> = request { api.createUser(dto) }
    suspend fun update(id: Int, dto: UpdateUserDto): Result<Unit> = request { api.updateUser(id, dto) }

    suspend fun toggleStatus(id: Int): Result<Unit> = jsonRequest { api.toggleUserStatus(id) }
    suspend fun resetPassword(id: Int, password: String): Result<Unit> = jsonRequest {
        api.resetUserPassword(id, ResetUserPasswordDto(id, password))
    }

    private suspend fun request(call: suspend () -> retrofit2.Response<ApiResponse<String>>): Result<Unit> = try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body?.success == true) Result.success(Unit)
        else Result.failure(Exception(body?.message ?: "Error HTTP ${response.code()}"))
    } catch (error: Exception) { Result.failure(error) }

    private suspend fun jsonRequest(call: suspend () -> retrofit2.Response<JsonObject>): Result<Unit> = try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body?.get("success")?.asBoolean == true) Result.success(Unit)
        else Result.failure(Exception(body?.get("message")?.asString ?: errorMessage(response.errorBody()?.string()) ?: "Error HTTP ${response.code()}"))
    } catch (error: Exception) { Result.failure(error) }

    private fun errorMessage(raw: String?): String? = try {
        raw?.let { JsonParser.parseString(it).asJsonObject.get("message")?.asString }
    } catch (_: Exception) { null }
}
