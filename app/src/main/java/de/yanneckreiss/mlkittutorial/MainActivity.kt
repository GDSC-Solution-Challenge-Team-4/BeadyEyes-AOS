package de.yanneckreiss.mlkittutorial

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.mlkittutorial.ui.dialog.DialogViewModel
import de.yanneckreiss.mlkittutorial.ui.MainScreen
import de.yanneckreiss.mlkittutorial.ui.Main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.RecordAndConvertToText
import de.yanneckreiss.mlkittutorial.ui.money.ui.MoneyScreen
import de.yanneckreiss.mlkittutorial.ui.pointer.PointerScreen
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeMLKitTutorialTheme
import de.yanneckreiss.mlkittutorial.ui.translate.TranslateViewModel


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val context: Context = LocalContext.current
            var textToSpeechInitialized by remember { mutableStateOf(false) }
            var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }

            var sttValue by remember { mutableStateOf("") }
            val dialogViewModel: DialogViewModel = viewModel()
            val mainViewModel: MainViewModel = viewModel()
            val translateViewModel: TranslateViewModel = viewModel()

            var showedText = "not found yet"

            fun initializeTextToSpeech() {
                if (!textToSpeechInitialized) {
                    textToSpeech = TextToSpeech(context) { status ->
                        if (status == TextToSpeech.SUCCESS) {
                            textToSpeechInitialized = true
                        }
                    }
                }
            }

            val permissionState = rememberPermissionState(
                permission = Manifest.permission.RECORD_AUDIO
            )
            SideEffect {
                permissionState.launchPermissionRequest()
            }

            val speechRecognizerLauncher = rememberLauncherForActivityResult(
                contract = RecordAndConvertToText(),
                onResult = {
                    sttValue = it.toString()
                }
            )

            JetpackComposeMLKitTutorialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
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

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var selectedTabIndex by remember {
                            mutableIntStateOf(1)
                        }

                        val pagerState = rememberPagerState {
                            tabItems.size
                        }

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
                                    } else
                                        permissionState.launchPermissionRequest()
                                },
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                                    .padding(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_record), // 이미지 리소스 지정
                                    contentDescription = "Record", // 이미지 버튼에 대한 설명
                                    colorFilter = ColorFilter.tint(Gray)
                                )
                            }
                            if (sttValue.isNotBlank()) {
                                Text(
                                    text = sttValue,
                                    fontSize = 24.sp
                                )
                            }
//                            }
//                            if (sttValue.isNotBlank()) {
//                                when(sttValue){
//                                    "포인터" -> PointerScreen()
//                                    "돈" -> MoneyScreen()
//                                    "텍스트"-> MainScreen()
//                                }
//                            }
                            IconButton(
                                onClick = {
                                    dialogViewModel.helpDialogOn()
                                    translateViewModel.OnlytextToSpeech(context ,"도움말 string")
                                },
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
                                    var string = ttsIndex(index)

//                                    when(sttValue){
//                                            "포인터" -> {
//                                                selectedTabIndex = 2
//                                            }
//                                            "돈" -> {
//                                                selectedTabIndex = 0
//                                            }
//                                            "텍스트"-> {
//                                                selectedTabIndex = 1
//                                            }
//                                    }
                                    LaunchedEffect(selectedTabIndex) {
                                        if (selectedTabIndex != pagerState.currentPage) {
                                            pagerState.scrollToPage(selectedTabIndex)
                                        }
                                    }
                                    when (index) {
                                        0 -> {
                                            initializeTextToSpeech()
                                            textToSpeech?.speak(
                                                string,
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                            MoneyScreen()
                                        }

                                        1 -> {
                                            initializeTextToSpeech()
                                            textToSpeech?.speak(
                                                string,
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                            MainScreen(mainViewModel)
                                        }

                                        2 -> {
                                            initializeTextToSpeech()
                                            textToSpeech?.speak(
                                                string,
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                            PointerScreen()
                                        }
                                    }
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    if (textToSpeechInitialized) {
                                        showedText = mainViewModel.state.value.detectedtext
                                        translateViewModel.OnlytextToSpeech(
                                            context,
                                            showedText
                                        )
                                        dialogViewModel.shortDialogOn()
                                    }
                                }, enabled = translateViewModel.state.value.isButtonEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp, bottom = 15.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_camera2),
                                    contentDescription = "TTS",
                                )
                            }
                        }
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

                } // 3줄짜리 작은 text dialog
                if (dialogViewModel.isFullDialogShown) {
                    AlertDialog(onDismissRequest = {
                        dialogViewModel.onDismissFullDialog()
                    }, confirmButton = {
                        Button(onClick = {
                            dialogViewModel.onDismissFullDialog()
                            dialogViewModel.shortDialogOn()
                        }
                        ) {
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
                    })
                }// 스크롤 되는 text dialog
                if (dialogViewModel.isHelpDialogShown) {
                    AlertDialog(onDismissRequest = {
                        dialogViewModel.onDismissHelpDialog()
                    }, confirmButton = {
                        Button(onClick = {

                        }) {
                            Text(text = "한번 더 듣기")
                        }
                    }, dismissButton = {
                        Button(onClick = {
                            dialogViewModel.onDismissHelpDialog()
                        }) {
                            Text(text = "나가기")
                        }
                    }, title = {
                        Text(
                            text = "도움말",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }, text = {
                        Text(
                            text = "도움말입니다 잘 읽어 보아요",
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }

                    )

                }// 도움말 dialog
            }
        }
    }
}

data class TabItem(
    var title: String,
    //var unSelectedIcon: ImageVector,
    var selectedIcon: Int
)

fun ttsIndex(index: Int): String {
    var string = ""
    when (index) {
        0 -> string = "Currancy Screen"
        1 -> string = "Text Screen"
        2 -> string = "Pointer Screen"
    }

    return string
}