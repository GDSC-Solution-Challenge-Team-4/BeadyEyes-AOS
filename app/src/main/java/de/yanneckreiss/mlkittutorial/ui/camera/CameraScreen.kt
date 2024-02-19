package de.yanneckreiss.mlkittutorial.ui.camera

import android.content.Context
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import de.yanneckreiss.cameraxtutorial.R
import androidx.lifecycle.viewmodel.compose.viewModel
import de.yanneckreiss.mlkittutorial.translate.TranslateViewModel
import de.yanneckreiss.mlkittutorial.ui.DialogViewModel

@Composable
fun CameraScreen() {
    CameraContent()
}

@Composable
private fun CameraContent(
    viewModel: TranslateViewModel = viewModel(),
    dialogViewModel: DialogViewModel = viewModel()
) {
    val state by viewModel.state
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }
    var detectedText: String by remember { mutableStateOf("No text detected yet..") }
    var showedText : String by remember { mutableStateOf("No text detected yet..") }
    // Text to speech related variables
    var textToSpeechInitialized by remember { mutableStateOf(false) }
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }



    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
    }

    fun initializeTextToSpeech() {
        if (!textToSpeechInitialized) {
            textToSpeech = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechInitialized = true
                }
            }
        }
    }


    var isPlaying: Boolean by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
        //topBar = { TopAppBar() },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier.padding(bottom = 75.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White)
                        .padding(5.dp),
                    contentAlignment = Alignment.TopEnd // 버튼 오른쪽 상단에 배치??
                ) {
                    IconButton(
                        onClick = {
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_question),
                            contentDescription = "question",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    factory = { context ->
                        PreviewView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setBackgroundColor(Color.BLACK)
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FILL_START
                        }.also { previewView ->
                            startTextRecognition(
                                context = context,
                                cameraController = cameraController,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                onDetectedTextUpdated = ::onTextUpdated
                            )
                        }.also {

                        }
                    }
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(16.dp)
            ) {

                val contentColor = LocalContentColor.current
                // Button for Text-to-Speech
                IconButton(
                    onClick = {
                        if (textToSpeechInitialized) {
                            showedText = detectedText
                            viewModel.OnlytextToSpeech(context, showedText)
                            dialogViewModel.shortDialogOn()
                        }
                    }, enabled = state.isButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_camera),
                        contentDescription = "TTS",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Initialize Text-to-Speech when the composable is first composed
    LaunchedEffect(Unit) {
        initializeTextToSpeech()
    }

    // Dispose of Text-to-Speech when the composable is removed
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
    if (dialogViewModel.isShortDialogShown) {
        AlertDialog(onDismissRequest = {
            dialogViewModel.onDismissShortDialog()
        }, confirmButton = {
           Button(onClick = {
               dialogViewModel.onDismissShortDialog()
               dialogViewModel.fullDialogOn()
           }) {
               Text(text = "확대")
           }
        }, dismissButton = {
            Button(onClick = {
                dialogViewModel.onDismissShortDialog()
            }) {
                Text(text = "나가기")
            }
        }, title = {
            Text(text = "감지된 문자")
        }, text = {
            Text(
                text = showedText,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        )

    }
    if (dialogViewModel.isFullDialogShown) {
        AlertDialog(onDismissRequest = {
            dialogViewModel.onDismissFullDialog()
        }, confirmButton = {
            Button(onClick = {
                dialogViewModel.onDismissFullDialog()
                dialogViewModel.shortDialogOn()
            }) {
                Text(text = "원래대로")
            }
        }, dismissButton = {
            Button(onClick = {
                dialogViewModel.onDismissFullDialog()
            }) {
                Text(text = "나가기")
            }
        }, title = {
            Text(text = "감지된 문자")
        }, text = {
            Text(
                text = showedText,
                Modifier.verticalScroll(rememberScrollState())
            )
        }

        )
    }
}



private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {

    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}


@Preview
@Composable
fun CameraPreview() {
    CameraScreen()
}