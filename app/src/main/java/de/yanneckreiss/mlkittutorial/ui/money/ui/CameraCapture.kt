package de.yanneckreiss.mlkittutorial.ui.money.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import com.google.common.util.concurrent.ListenableFuture
import de.yanneckreiss.mlkittutorial.util.classifyImage
import de.yanneckreiss.mlkittutorial.util.loadImageBufferFromBitmap
import java.util.concurrent.Executor


@Composable
fun CameraContentMoney(context: Context, modifier: Modifier = Modifier) {
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCaptureExecutor = ContextCompat.getMainExecutor(context)
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    val detectedText = remember { mutableStateOf("No text detected yet..") }

    Box {
        CameraPreview(imageCaptureExecutor,cameraProviderFuture, imageCapture, lifecycleOwner)
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            CaptureButton(
                onClick = {
                    captureImage(
                        context,
                        imageCapture = imageCapture,
                        imageCaptureExecutor,
                        detectedText
                    )
                },
                modifier = Modifier.padding(16.dp)
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
}

@Composable
fun CameraPreview(
    imageCaptureExecutor: Executor,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_START
        }
    }

    val cameraProvider = cameraProviderFuture.get()

    val previewUseCase = remember() {
        Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
    }
    val cameraSelector = remember {
        CameraSelector.DEFAULT_BACK_CAMERA
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .build()
        .apply {
            setAnalyzer(imageCaptureExecutor, ImageAnalysis.Analyzer { image ->
                image.close()
            })
        }


    LifecycleStartEffect(previewUseCase, cameraSelector, imageCapture, imageAnalysis, lifecycleOwner = lifecycleOwner) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase, imageCapture, imageAnalysis)
            Log.d("MainActivity25", "바인딩 성공")
        }catch (e: Exception){
            Log.d("MainActivity25", "바인딩 실패 $e")
        }
        onStopOrDispose {
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { previewView }
    )
}


@Composable
fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text("Capture")
    }
}

fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    imageCaptureExecutor: Executor,
    detectedText: MutableState<String>
) {
    val file = createTempFile(context)
    Log.d("MainActivity24", "${createTempFile(context)}")
    val outputFileOptions =
        ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputFileOptions,
        imageCaptureExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                //Log.d("MainActivity22", "Image saved: ${file.absolutePath}")
                // Add your code here
                var bitmapImage = BitmapFactory.decodeFile(file.absolutePath)
                var bitmap2 = Bitmap.createScaledBitmap(bitmapImage, 224, 224, false)
                Log.d("MainActivity27", "Image saved: ${file.absolutePath}")
                //detectedText.value = classifyImage(context, bitmap2)
                Log.d("MainActivity27", "Image saved: ${file.absolutePath}")
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                Log.d(
                    "MainActivity23",
                    "Error capturing image: ${exception.message}",
                    exception
                )
                // Add your error handling here
            }
        }
    )
}

fun createTempFile(context: Context): java.io.File {
    val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
                                .format(java.util.Date())
    val imageFileName = "JPEG_$timeStamp.jpg"
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
}