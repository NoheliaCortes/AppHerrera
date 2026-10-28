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
            val response = apiService.getProductCatalog(search = search, lineId = lineId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener catálogo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductStats(): Result<ProductStatsDto> {
        return try {
            val response = apiService.getProductStats()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener estadísticas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}