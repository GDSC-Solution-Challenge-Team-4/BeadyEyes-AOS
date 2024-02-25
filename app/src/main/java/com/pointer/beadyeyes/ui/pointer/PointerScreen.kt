package com.pointer.beadyeyes.ui.pointer


import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.pointer.beadyeyes.ui.main.MainViewModel
import com.pointer.beadyeyes.ui.money.CameraContentMoney
import com.pointer.beadyeyes.ui.pointer.network.PointerBackendResponse
import com.pointer.beadyeyes.ui.pointer.network.PointerService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Composable
fun PointerScreen(index: Int, mainViewModel: MainViewModel,modifier: Modifier = Modifier) {

    val context: Context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CameraContentMoney(
            context = context,
            index = index,
            onResult = {
                pointer(it, mainViewModel)
            }
        )
    }


}

fun pointer(file: String, mainViewModel: MainViewModel) {

    var result : String

    val api = PointerService.getInstance()
    val file1 = File(file)
    //Log.d("포인터 File Size", "파일 크기: $fileSize 바이트")

    val requestFile = file1.asRequestBody("image/jpeg".toMediaTypeOrNull())//"image/jpeg"
    val body = MultipartBody.Part.createFormData("image", file1.name, requestFile)

    Log.d("포인터 파일 타입", file1::class.java.toString())
    Log.d("포인터 바디", body.toString())
    Log.d("포인터 리퀘스트 파일", requestFile.toString())

    api.postPointerImage(body).enqueue(object : Callback<PointerBackendResponse> {
        override fun onResponse(
            call: Call<PointerBackendResponse>,
            response: Response<PointerBackendResponse>
        ) {
            Log.d("포인터 성공", "Response code: ${response.code()}")
            Log.d("포인터 통신", response.body().toString())
            result = response.body()?.resultData.toString()
            //Log.d("포인터 msg", result)
            mainViewModel.onTextValueChange(result)
        }
        override fun onFailure(call: Call<PointerBackendResponse>, t: Throwable) {
            //Log.e("포인터 네트워크 오류", "Unknown error: ${t.message}", t)
        }
    })
}