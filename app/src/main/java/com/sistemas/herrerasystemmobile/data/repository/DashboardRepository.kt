package com.sistemas.herrerasystemmobile.data.repository

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.*
import retrofit2.Response
import java.time.LocalDate
import java.time.ZoneId

class DashboardRepository(private val api: ApiService) {
    suspend fun load(adminName: String): Result<AdminDashboardDataDto> = try {
        val today = LocalDate.now(ZoneId.of("America/Managua"))
        val statsResponse = api.getSalesStats()
        val statsEnvelope = statsResponse.body()
        if (!statsResponse.isSuccessful || statsEnvelope?.success != true || statsEnvelope.data == null)
            error(statsEnvelope?.message ?: "Error HTTP ${statsResponse.code()} al cargar estadísticas")

        val salesResponse = api.getSales(today.toString(), today.toString())
        val salesEnvelope = salesResponse.body()
        if (!salesResponse.isSuccessful || salesEnvelope?.success != true || salesEnvelope.data == null)
            error(salesEnvelope?.message ?: "Error HTTP ${salesResponse.code()} al cargar ventas de hoy")

        val sales = salesEnvelope.data.data
        val salesToday = sales.sumOf { it.total }
        val monthlyStats = statsEnvelope.data
        val average = if (monthlyStats.salesThisMonth == 0) 0.0 else monthlyStats.totalIncomeThisMonth / monthlyStats.salesThisMonth
        Result.success(AdminDashboardDataDto(
            adminName = adminName,
            userRole = "Administrador",
            dateText = today.toString(),
            salesToday = salesToday,
            pendingOrders = null,
            productsSoldThisMonth = monthlyStats.productsSoldThisMonth,
            averageTicket = average,
            monthlyIncome = monthlyStats.totalIncomeThisMonth
        ))
    } catch (error: Exception) { Result.failure(error) }
}
