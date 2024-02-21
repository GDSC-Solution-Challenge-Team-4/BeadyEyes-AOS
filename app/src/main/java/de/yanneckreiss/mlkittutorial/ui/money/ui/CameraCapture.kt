package de.yanneckreiss.mlkittutorial.ui.money.ui

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.util.concurrent.ListenableFuture
import de.yanneckreiss.cameraxtutorial.R
import java.util.concurrent.Executor


@Composable
fun CameraContentMoney(context: Context, modifier: Modifier=Modifier) {
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    val imageCaptureExecutor = ContextCompat.getMainExecutor(context)

    Column {
        CameraPreview(context,cameraProviderFuture, lifecycleOwner)
        CaptureButton(
            onClick = {
                captureImage(context, lifecycleOwner, imageCaptureExecutor)
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CameraPreview(
    context: Context,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier,
) {
    val previewView = remember { PreviewView(ContextThemeWrapper(context, R.style.Theme_JetpackComposeMLKitTutorial)).apply {
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        scaleType = PreviewView.ScaleType.FILL_START
    } }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = { previewView }
    ) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.setSurfaceProvider(it.surfaceProvider)
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    }
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
    lifecycleOwner: LifecycleOwner,
    imageCaptureExecutor: Executor
) {
    val file = createTempFile(context)
    val imageCapture = androidx.camera.core.ImageCapture.Builder()
        .build()

    val outputFileOptions = androidx.camera.core.ImageCapture.OutputFileOptions.Builder(file).build()

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)


        imageCapture.takePicture(
            outputFileOptions,
            imageCaptureExecutor,
            object : androidx.camera.core.ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: androidx.camera.core.ImageCapture.OutputFileResults) {
                    android.util.Log.d("MainActivity", "Image saved: ${file.absolutePath}")
                    // Add your code here
                }

                override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                    android.util.Log.e("MainActivity", "Error capturing image: ${exception.message}", exception)
                    // Add your error handling here
                }
            }
        )
    }, imageCaptureExecutor)
}

fun createTempFile(context: Context): java.io.File {
    val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
    val imageFileName = "JPEG_$timeStamp.jpg"
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
}