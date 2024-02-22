package de.yanneckreiss.mlkittutorial.ui.pointer.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MyApi {
    private const val BASE_URL = "http://35.216.66.135:8080"
    private var apiService: ApiService? = null

    fun getInstance(): ApiService {
        if (apiService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}