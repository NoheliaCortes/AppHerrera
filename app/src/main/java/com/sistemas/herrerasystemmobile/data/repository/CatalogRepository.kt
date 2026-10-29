package com.sistemas.herrerasystemmobile.data.repository

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductCatalogDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductStatsDto

class CatalogRepository(private val apiService: ApiService) {

    suspend fun getProductCatalog(
        search: String? = null,
        lineId: Int? = null
    ): Result<List<ProductCatalogDto>> {
        return try {
            val products = mutableListOf<ProductCatalogDto>()
            var page = 1
            var hasNextPage: Boolean
            do {
                val response = apiService.getProductCatalog(
                    search = search,
                    lineId = lineId,
                    active = null,
                    page = page,
                    pageSize = 50
                )
                val envelope = response.body()
                if (!response.isSuccessful || envelope?.success != true || envelope.data == null) {
                    return Result.failure(
                        Exception(envelope?.message?.takeIf { it.isNotBlank() } ?: "Error HTTP ${response.code()} al obtener catálogo")
                    )
                }
                products += envelope.data.data
                hasNextPage = envelope.data.hasNextPage
                page++
            } while (hasNextPage)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductStats(): Result<ProductStatsDto> {
        return try {
            val response = apiService.getProductStats()
            val envelope = response.body()
            if (response.isSuccessful && envelope?.success == true && envelope.data != null) {
                Result.success(envelope.data)
            } else {
                Result.failure(Exception(envelope?.message?.takeIf { it.isNotBlank() } ?: "Error HTTP ${response.code()} al obtener estadísticas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
