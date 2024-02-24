package de.yanneckreiss.mlkittutorial.ui.pointer.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

//interface PointerService {
//    @POST("/image/pointer")
//    fun postPointerImage(@Body jsonParams: PointerModel): Call<PointerBackendResponse>
//}