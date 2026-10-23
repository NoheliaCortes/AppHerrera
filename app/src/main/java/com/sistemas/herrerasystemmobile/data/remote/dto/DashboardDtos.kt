package com.sistemas.herrerasystemmobile.data.remote.dto

// Modelo de Accesos Rápidos
data class QuickAccessItemDto(
    val id: String,
    val title: String,
    val iconName: String,
    val route: String
)

// Modelo de Tarjeta KPI
data class KpiCardDto(
    val id: String,
    val title: String,
    val value: String,
    val subtitle: String? = null,
    val type: KpiType
)

enum class KpiType {
    VENTAS_DIA, PEDIDOS_PENDIENTES, STOCK_CRITICO, TICKET_PROMEDIO
}

// Modelo de Transacción Reciente
data class RecentTransactionDto(
    val id: String,
    val clientName: String,
    val detail: String,
    val timeAgo: String,
    val amount: String,
    val tagText: String, // "Venta", "Pedido", "Reabastecimiento"
    val tagType: TransactionTagType
)

enum class TransactionTagType {
    VENTA, PEDIDO, REABASTECIMIENTO
}

// DTO Contenedor del Dashboard Admin
data class AdminDashboardDataDto(
    val adminName: String,
    val userRole: String,
    val dateText: String,
    val quickAccess: List<QuickAccessItemDto>,
    val kpis: List<KpiCardDto>,
    val recentTransactions: List<RecentTransactionDto>
)