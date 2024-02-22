package de.yanneckreiss.mlkittutorial.ui.pointer.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

const val BASE_URL = "http://35.216.66.135:8080"
interface ApiService {
    @Multipart
    @POST("${BASE_URL}/image/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<PointerModel>
}