package com.sistemas.herrerasystemmobile.data.repository

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import com.sistemas.herrerasystemmobile.data.remote.dto.*

class ClientRepository(private val api: ApiService) {
    suspend fun getClients(search: String? = null): Result<List<ClientDto>> {
      return try {
        val clients = mutableListOf<ClientDto>()
        var page = 1
        do {
            val response = api.getCustomers(page, 50, search)
            val envelope = response.body()
            if (!response.isSuccessful || envelope?.success != true || envelope.data == null)
                return Result.failure(Exception(envelope?.message ?: "Error HTTP ${response.code()} al cargar clientes"))
            clients += envelope.data.data
            val hasNext = envelope.data.hasNextPage
            page++
            if (!hasNext) break
        } while (true)
        Result.success(clients)
      } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getStats(): Result<ClientStatsDto> = request { api.getCustomerStats() }
    suspend fun create(dto: ClientCreateDto): Result<ClientDto> = request { api.createCustomer(dto) }
    suspend fun update(id: Int, dto: ClientUpdateDto): Result<Unit> = try {
        val r = api.updateCustomer(id, dto); val b = r.body()
        if (r.isSuccessful && b?.success == true) Result.success(Unit) else Result.failure(Exception(b?.message ?: "Error HTTP ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
    suspend fun delete(id: Int): Result<Unit> = try {
        val r = api.deleteCustomer(id); val b = r.body()
        if (r.isSuccessful && b?.success == true) Result.success(Unit) else Result.failure(Exception(b?.message ?: "Error HTTP ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    private suspend fun <T> request(call: suspend () -> retrofit2.Response<ApiResponse<T>>): Result<T> = try {
        val r = call(); val b = r.body()
        if (r.isSuccessful && b?.success == true && b.data != null) Result.success(b.data)
        else Result.failure(Exception(b?.message ?: "Error HTTP ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }
}
