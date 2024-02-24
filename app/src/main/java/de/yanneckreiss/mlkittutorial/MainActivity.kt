package de.yanneckreiss.mlkittutorial

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.mlkittutorial.ui.MainScreen
import de.yanneckreiss.mlkittutorial.ui.RecordAndConvertToText
import de.yanneckreiss.mlkittutorial.ui.money.ui.MoneyScreen
import de.yanneckreiss.mlkittutorial.ui.money.ui.captureImage
import de.yanneckreiss.mlkittutorial.ui.pointer.PointerScreen
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeMLKitTutorialTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context: Context = LocalContext.current
            var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
            var sttValue by remember { mutableStateOf("") }
            val speechRecognizerLauncher = rememberLauncherForActivityResult(
                contract = RecordAndConvertToText(),
                onResult = { sttValue = it.toString() }
            )


            JetpackComposeMLKitTutorialTheme {
                val tabItems = listOf(
                    TabItem(
                        title = "money",
                        selectedIcon = R.drawable.icon_money
                    ),
                    TabItem(
                        title = "ocr",
                        selectedIcon = R.drawable.icon_ocr
                    ),
                    TabItem(
                        title = "pointer",
                        selectedIcon = R.drawable.icon_pointer
                    )
                )
                val permissionState =
                    rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
                val pagerState = rememberPagerState { tabItems.size }
                LaunchedEffect(Unit) {
                    textToSpeech = TextToSpeech(context) {}
                }
                SideEffect {
                    permissionState.launchPermissionRequest()
                }
                LaunchedEffect(pagerState.currentPage) {
                    textToSpeech?.speak(
                        ttsIndex(pagerState.currentPage),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(
                                onClick = {
                                    if (permissionState.status.isGranted) {
                                        speechRecognizerLauncher.launch(Unit)
                                    } else {
                                        permissionState.launchPermissionRequest()
                                    }
                                },
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_record), // 이미지 리소스 지정
                                    contentDescription = "Page Move Button With Voice Recording", // 이미지 버튼에 대한 설명
                                    colorFilter = ColorFilter.tint(Gray),
                                )
                            }
//                            if (sttValue.isNotBlank()) {
//                                Text(text = sttValue, fontSize = 24.sp)
//                            }
                            IconButton(
                                onClick = { },
                                modifier = Modifier
                                    .padding(5.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_question), // 이미지 리소스 지정
                                    contentDescription = "Question", // 이미지 버튼에 대한 설명
                                    colorFilter = ColorFilter.tint(Gray)
                                )
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index == pagerState.currentPage) {
                                    when (index) {
                                        0 -> MainScreen()
                                        1 -> MoneyScreen(index=index)
                                        2 -> PointerScreen(index=index)
                                    }
                                }
                                LaunchedEffect(sttValue){
                                    //TextToSpeech?.speak(ttsIndex(pagerState.currentPage),TextToSpeech.QUEUE_FLUSH,null,null)
                                    when(sttValue){
                                        "[텍스트]","[text]" -> pagerState.scrollToPage(0)
                                        "[돈]","[지폐]","","[money]","[currency]" -> pagerState.scrollToPage(1)
                                        "[포인터]","[pointer]" -> pagerState.scrollToPage(2)
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            IconButton(
                                onClick = {
                                          when(pagerState.currentPage){
                                              0->
                                              {

                                              }
                                              1->
                                              {

                                              }
                                              2->
                                              {

                                              }
                                          }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp, bottom = 15.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_camera2),
                                    contentDescription = "TTS Button"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TabItem(
    var title: String,
    var selectedIcon: Int
)

fun ttsIndex(index: Int): String {
    var string = ""
    when (index) {
        0 -> string = "Text Screen"
        1 -> string = "Currency Screen"
        2 -> string = "Pointer Screen"
    }
    return string
}