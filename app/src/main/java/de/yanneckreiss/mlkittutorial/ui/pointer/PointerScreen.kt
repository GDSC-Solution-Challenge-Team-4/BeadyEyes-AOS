package de.yanneckreiss.mlkittutorial.ui.pointer


import android.content.Context
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.yanneckreiss.mlkittutorial.ui.pointer.ui.theme.JetpackComposeCameraXMLKitTutorialTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import de.yanneckreiss.mlkittutorial.ui.money.ui.CameraContentMoney
import de.yanneckreiss.mlkittutorial.ui.pointer.network.PointerBackendResponse
import de.yanneckreiss.mlkittutorial.ui.pointer.network.PointerService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

@Composable
fun PointerScreen(index: Int, modifier: Modifier = Modifier) {

    val context: Context = LocalContext.current
    var showMessage by remember { mutableStateOf(false) }
    var filePathResult by remember { mutableStateOf("") }
    val detectedText = remember { mutableStateOf("No text detected yet..") }

    val alpha by animateFloatAsState(
        targetValue = if (showMessage) 1f else 0f,
        animationSpec = if (showMessage) {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        } else {
            keyframes {
                durationMillis = 1000
                1.0f at 0 // fade out 완료
                0.0f at 1 using FastOutSlowInEasing // fade out 시작

            }
        }, label = ""
    )

    LaunchedEffect(Unit) {
        showMessage = true
        delay(2000)
        showMessage = false
    }

    if (showMessage) {
        Box(
            modifier = Modifier
                .alpha(alpha)
                .graphicsLayer(alpha = alpha)
                .fillMaxSize()
                .padding(top = 50.dp),
            contentAlignment = Alignment.TopCenter // 세로 정렬을 맨 위로 설정
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Gray, shape = RoundedCornerShape(20.dp))
                    .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pointer Screen",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CameraContentMoney(
            context = context,
            index = index,
            onResult = {
                filePathResult = it
                Log.d("newResult", "Response code: $filePathResult")
                pointer(filePathResult, detectedText)

            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Yellow, shape = RoundedCornerShape(8.dp))
                .padding(10.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = detectedText.value,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun PointerPreview() {
    JetpackComposeCameraXMLKitTutorialTheme {
        //PointerScreen()
    }
}


fun pointer(file: String, detectedText: MutableState<String>) {

    var result : String

    val api = PointerService.getInstance()
    val file1 = File(file)
    val fileSize = file1.length()
    Log.d("포인터 File Size", "파일 크기: $fileSize 바이트")

    val requestFile = file1.asRequestBody("form-data/*".toMediaTypeOrNull())//"image/jpeg"
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
            Log.d("포인터 msg", result)
            detectedText.value = result
        }
        override fun onFailure(call: Call<PointerBackendResponse>, t: Throwable) {
            Log.e("포인터 네트워크 오류", "Unknown error: ${t.message}", t)
        }
    })
}