package com.sistemas.herrerasystemmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

// Wrapper de respuesta genérico
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

// Wrapper de paginación
data class PagedResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalRecords") val totalRecords: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPreviousPage") val hasPreviousPage: Boolean
)

// DTO del Catálogo Enriquecido (GET /api/Products/catalog)
data class ProductCatalogDto(
    @SerializedName("id") val id: Int,
    @SerializedName("productName") val productName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("lineName") val lineName: String,
    @SerializedName("flavorName") val flavorName: String,
    @SerializedName("presentationName") val presentationName: String,
    @SerializedName("wholesalePrice") val wholesalePrice: Double?,
    @SerializedName("retailPrice") val retailPrice: Double?
)

// DTO para Estadísticas de Productos (GET /api/Products/stats)
data class ProductStatsDto(
    @SerializedName("totalProducts") val totalProducts: Int,
    @SerializedName("activeProducts") val activeProducts: Int,
    @SerializedName("inactiveProducts") val inactiveProducts: Int
)