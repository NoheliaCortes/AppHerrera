package com.sistemas.herrerasystemmobile.data.remote.retrofit

import com.sistemas.herrerasystemmobile.data.remote.api.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL publica en Azure App Service
    const val API_ORIGIN = "https://api-herrera-deega3a2e8d8cbge.canadacentral-01.azurewebsites.net"
    private const val BASE_URL = "$API_ORIGIN/api/"

    fun getApiService(): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
