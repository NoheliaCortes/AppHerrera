package com.sistemas.herrerasystemmobile.data.remote.api

import com.sistemas.herrerasystemmobile.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.PATCH
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

    @GET("Customers")
    suspend fun getCustomers(@Query("page") page: Int = 1, @Query("pageSize") pageSize: Int = 50,
        @Query("search") search: String? = null, @Query("departmentId") departmentId: Int? = null,
        @Query("municipalityId") municipalityId: Int? = null): Response<ApiResponse<PagedResponse<ClientDto>>>

    @GET("Customers/stats")
    suspend fun getCustomerStats(): Response<ApiResponse<ClientStatsDto>>
    @GET("Customers/{id}")
    suspend fun getCustomer(@retrofit2.http.Path("id") id: Int): Response<ApiResponse<ClientDto>>
    @POST("Customers")
    suspend fun createCustomer(@Body request: ClientCreateDto): Response<ApiResponse<ClientDto>>
    @retrofit2.http.PUT("Customers/{id}")
    suspend fun updateCustomer(@retrofit2.http.Path("id") id: Int, @Body request: ClientUpdateDto): Response<ApiResponse<Any>>
    @retrofit2.http.DELETE("Customers/{id}")
    suspend fun deleteCustomer(@retrofit2.http.Path("id") id: Int): Response<ApiResponse<Any>>

    @GET("Users")
    suspend fun getUsers(): Response<ApiResponse<List<UserDto>>>

    @POST("Users")
    suspend fun createUser(@Body request: CreateUserDto): Response<ApiResponse<String>>

    @PUT("Users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body request: UpdateUserDto): Response<ApiResponse<String>>

    @PATCH("Users/{id}/toggle-status")
    suspend fun toggleUserStatus(@Path("id") id: Int): Response<com.google.gson.JsonObject>

    @PATCH("Users/{id}/reset-password")
    suspend fun resetUserPassword(@Path("id") id: Int, @Body request: ResetUserPasswordDto): Response<com.google.gson.JsonObject>

    @GET("Reports/sales-by-line-flavor")
    suspend fun getSalesByLineFlavor(@Query("startDate") startDate: String, @Query("endDate") endDate: String): Response<ApiResponse<List<SalesByLineFlavorDto>>>

    @GET("Reports/monthly-sales-trend")
    suspend fun getMonthlySalesTrend(@Query("startDate") startDate: String, @Query("endDate") endDate: String): Response<ApiResponse<List<MonthlySalesTrendDto>>>

    @GET("Reports/sales-by-geography")
    suspend fun getSalesByGeography(@Query("startDate") startDate: String, @Query("endDate") endDate: String): Response<ApiResponse<List<SalesByGeographyDto>>>

    @GET("Reports/top-customers")
    suspend fun getTopCustomers(@Query("startDate") startDate: String, @Query("endDate") endDate: String): Response<ApiResponse<List<TopCustomerDto>>>

    @GET("Reports/sales-by-presentation")
    suspend fun getSalesByPresentation(@Query("startDate") startDate: String, @Query("endDate") endDate: String): Response<ApiResponse<List<SalesByPresentationDto>>>
}
