package de.yanneckreiss.mlkittutorial.ui.camera

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextRecognitionAnalyzer(
    private val onDetectedTextUpdated: (String) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        const val THROTTLE_TIMEOUT_MS = 1_000L //몇초마다 분석 할건지 (같은 text 중복 번역 방지)
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()) //THROTTLE_TIMEOUT_MS 위해 사용
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) //ML kit 가져오기
    // When using Korean script library
    private val koreanTextRecognizer : TextRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        scope.launch {
            val mediaImage: Image = imageProxy.image ?: run { imageProxy.close(); return@launch }
            val inputImage: InputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            suspendCoroutine { continuation ->
//                textRecognizer.process(inputImage)
//                    .addOnSuccessListener { visionText: Text ->
//                        val detectedText: String = visionText.text
//                        if (detectedText.isNotBlank()) {
//                            onDetectedTextUpdated(detectedText)
//                        }
//                    }
//                    .addOnCompleteListener {
//                        continuation.resume(Unit) //다 했으면 코루틴 resume
//                    }
                koreanTextRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText: Text ->
                        val detectedText: String = visionText.text
                        if (detectedText.isNotBlank()) {
                            onDetectedTextUpdated(detectedText)
                        }
                    }
                    .addOnCompleteListener {
                        continuation.resume(Unit) //다 했으면 코루틴 resume
                    }
            }

            delay(THROTTLE_TIMEOUT_MS) //딜레이 걸기
        }.invokeOnCompletion { exception ->
            exception?.printStackTrace()
            imageProxy.close()
        }
    }
}
