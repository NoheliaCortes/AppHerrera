package com.sistemas.herrerasystemmobile.data.repository

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import retrofit2.Response

class ReportRepository(private val api: ApiService) {
    suspend fun salesByLineFlavor(start: String, end: String) = request { api.getSalesByLineFlavor(start, end) }
    suspend fun monthlyTrend(start: String, end: String) = request { api.getMonthlySalesTrend(start, end) }
    suspend fun geography(start: String, end: String) = request { api.getSalesByGeography(start, end) }
    suspend fun topCustomers(start: String, end: String) = request { api.getTopCustomers(start, end) }
    suspend fun presentations(start: String, end: String) = request { api.getSalesByPresentation(start, end) }

    private suspend fun <T> request(call: suspend () -> Response<ApiResponse<T>>): Result<T> = try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) Result.success(body.data)
        else Result.failure(Exception(body?.message ?: "Error HTTP ${response.code()} al cargar el reporte"))
    } catch (error: Exception) { Result.failure(error) }
}
