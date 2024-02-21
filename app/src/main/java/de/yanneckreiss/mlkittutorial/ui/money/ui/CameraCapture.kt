package de.yanneckreiss.mlkittutorial.ui.money.ui

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor


@Composable
fun CameraContentMoney(context: Context, modifier: Modifier = Modifier) {
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCaptureExecutor = ContextCompat.getMainExecutor(context)
    val imageCapture = ImageCapture.Builder()
        .build()

    Column {
        CameraPreview(cameraProviderFuture, imageCapture, lifecycleOwner)
        CaptureButton(
            onClick = { captureImage(context, imageCapture = imageCapture, imageCaptureExecutor) },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CameraPreview(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_START
        }
    }
    val previewUseCase = remember(previewView) {
        Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
    }
    val cameraSelector = remember {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
    val cameraProvider = cameraProviderFuture.get()
    LaunchedEffect(previewUseCase, cameraSelector) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, previewUseCase)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
    ) {
        Text("Capture")
    }
}

fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    imageCaptureExecutor: Executor
) {
    val file = createTempFile(context)
    val outputFileOptions =
        ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        outputFileOptions,
        imageCaptureExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                android.util.Log.d("MainActivity", "Image saved: ${file.absolutePath}")
                // Add your code here
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                android.util.Log.e(
                    "MainActivity",
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