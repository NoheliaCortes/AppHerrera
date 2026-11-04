package com.sistemas.herrerasystemmobile.data.remote.api

import com.sistemas.herrerasystemmobile.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
import com.sistemas.herrerasystemmobile.data.remote.dto.LoginRequestDto
import com.sistemas.herrerasystemmobile.data.remote.dto.LoginResponseDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductCatalogDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ProductStatsDto
import com.sistemas.herrerasystemmobile.data.remote.dto.ApiResponse
import com.sistemas.herrerasystemmobile.data.remote.dto.PagedResponse

interface ApiService {

    @POST("Auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    // Endpoints de Productos
    @GET("Products/catalog")
    suspend fun getProductCatalog(
        @Query("search") search: String? = null,
        @Query("lineId") lineId: Int? = null,
        @Query("presentationId") presentationId: Int? = null,
        @Query("flavorId") flavorId: Int? = null,
        @Query("active") active: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<ApiResponse<PagedResponse<ProductCatalogDto>>>

    @GET("Products/stats")
    suspend fun getProductStats(): Response<ApiResponse<ProductStatsDto>>
}
