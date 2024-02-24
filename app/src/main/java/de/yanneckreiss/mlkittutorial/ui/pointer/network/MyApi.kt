package de.yanneckreiss.mlkittutorial.ui.pointer.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface PointerService {

    @Multipart
    @POST("/image/pointer")
    fun postPointerImage(@Part image: MultipartBody.Part): Call<PointerBackendResponse>

    companion object PointerApi {
        private const val CONNECT_TIMEOUT = 300L // in seconds
        private const val READ_TIMEOUT = 300L // in seconds
        private const val BASE_URL = "http://35.216.106.76:8080"
        private var apiService: PointerService? = null

        fun getInstance(): PointerService {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build()


            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiService = retrofit.create(PointerService::class.java)
            }

            return apiService!!
        }
    }
}

