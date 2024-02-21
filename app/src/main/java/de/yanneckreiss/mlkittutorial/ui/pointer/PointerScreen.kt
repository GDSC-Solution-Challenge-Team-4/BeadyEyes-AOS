package de.yanneckreiss.mlkittutorial.ui.pointer


import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import de.yanneckreiss.mlkittutorial.ui.camera.startTextRecognition
import de.yanneckreiss.mlkittutorial.ui.pointer.ui.theme.JetpackComposeCameraXMLKitTutorialTheme
import kotlinx.coroutines.delay

@Composable
fun PointerScreen(modifier: Modifier = Modifier) {

    var showMessage by remember { mutableStateOf(false) }

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

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues: PaddingValues ->

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
                    setBackgroundColor(android.graphics.Color.BLACK)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_START
                }
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
                text = "detectedText",
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
        PointerScreen()
    }
}