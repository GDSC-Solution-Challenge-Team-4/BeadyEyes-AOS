package com.pointer.beadyeyes.ui.money

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleStartEffect
import com.google.common.util.concurrent.ListenableFuture
import com.pointer.beadyeyes.R
import com.pointer.beadyeyes.util.classifyImage
import kotlinx.coroutines.delay
import java.io.File
import java.lang.Math.min
import java.util.Locale
import java.util.concurrent.Executor


@Composable
fun CameraContentMoney(
    context: Context,
    index: Int,
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCaptureExecutor = remember { ContextCompat.getMainExecutor(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    var result: String by remember { mutableStateOf("") }

    Box {
        CameraPreview(imageCaptureExecutor, cameraProviderFuture, imageCapture, lifecycleOwner)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            IconButton(
                onClick = {
                    captureImage(
                        context,
                        imageCapture = imageCapture,
                        imageCaptureExecutor,
                        onResult = {
                            result = it
                            onResult(result)
                        },
                        index
                    )
                },
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
                    .width(50.dp)
                    .height(50.dp)
            ) {
                Image(
                    painter =painterResource(id = R.drawable.icon_camera2),
                    contentDescription = "Capture Button"
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


    LifecycleStartEffect(
        previewUseCase,
        cameraSelector,
        imageCapture,
        imageAnalysis,
        lifecycleOwner = lifecycleOwner
    ) {
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewUseCase,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception) {
        }
        onStopOrDispose {  }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = { previewView }
    )
}


fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    imageCaptureExecutor: Executor,
    onResult: (String) -> Unit,
    index: Int
) {
    val file = createTempFile(context)
    val outputFileOptions =
        ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputFileOptions,
        imageCaptureExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                if (index == 1) {
                    var bitmapImage = BitmapFactory.decodeFile(file.absolutePath)
                    var bitmap = ThumbnailUtils.extractThumbnail(
                        bitmapImage,
                        min(bitmapImage.width, bitmapImage.height),
                        min(bitmapImage.width, bitmapImage.height)
                    );
                    bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false)
                    onResult(classifyImage(context, bitmap))
                } else {
                    onResult(file.absolutePath)
                }
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
            }
        }
    )

}

fun createTempFile(context: Context): File {
    val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        .format(java.util.Date())
    val imageFileName = "JPEG_$timeStamp"
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return File.createTempFile(imageFileName, ".jpeg", storageDir)
}