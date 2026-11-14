package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SalesSummaryDto(
    @SerializedName("salesThisMonth") val salesThisMonth: Int = 0,
    @SerializedName("totalIncomeThisMonth") val totalIncomeThisMonth: Double = 0.0,
    @SerializedName("productsSoldThisMonth") val productsSoldThisMonth: Int = 0
)

data class SaleSummaryDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("saleCode") val saleCode: String = "",
    @SerializedName("saleDate") val saleDate: String = "",
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("total") val total: Double = 0.0
)

data class AdminDashboardDataDto(
    val adminName: String,
    val userRole: String,
    val dateText: String,
    val salesToday: Double = 0.0,
    val pendingOrders: Int? = null,
    val productsSoldThisMonth: Int = 0,
    val averageTicket: Double = 0.0,
    val monthlyIncome: Double = 0.0
)
