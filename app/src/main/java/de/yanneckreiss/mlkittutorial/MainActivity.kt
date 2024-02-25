package de.yanneckreiss.mlkittutorial

import android.Manifest
import android.content.Context
import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.BalloonWindow
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setArrowColor
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.mlkittutorial.ui.Main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.MainScreen
import de.yanneckreiss.mlkittutorial.ui.RecordAndConvertToText
import de.yanneckreiss.mlkittutorial.ui.dialog.DialogViewModel
import de.yanneckreiss.mlkittutorial.ui.money.ui.MoneyScreen
import de.yanneckreiss.mlkittutorial.ui.money.ui.captureImage
import de.yanneckreiss.mlkittutorial.ui.pointer.PointerScreen
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeMLKitTutorialTheme
import kotlinx.coroutines.launch
import java.sql.Types.NULL


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context: Context = LocalContext.current
            var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
            var sttValue by remember { mutableStateOf("") }
            var pageName by remember { mutableStateOf("") }
            val dialogViewModel: DialogViewModel = viewModel()
            val mainViewModel: MainViewModel = viewModel()

            var showedText = remember { mutableStateOf("No text detected yet..") }

            var backPressedState by remember { mutableStateOf(true) }
            var backPressedTime = 0L


            var textVisible by remember { mutableStateOf(false) }
            var isFullView by remember { mutableStateOf(false) }
            val lifecycleOwner = LocalLifecycleOwner.current
            var balloonWindow: BalloonWindow? by remember { mutableStateOf(null) }
            val scrollState = rememberScrollState()

            //tts

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
            val balloonBuilder = rememberBalloonBuilder {
                setWidthRatio(1.0f)
                setWidth(BalloonSizeSpec.WRAP) // sets width size depending on the content's size.
                setHeight(BalloonSizeSpec.WRAP) // sets height size depending on the content's size.
                setText("Edit your profile here!")
                setTextColorResource(R.color.white)
                setTextSize(15f)
                setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                setArrowSize(10)
                setArrowPosition(0.5f)
                setPadding(12)
                setCornerRadius(8f)
                setBackgroundColorResource(R.color.white)
                setBalloonAnimation(BalloonAnimation.ELASTIC)
                build()
            }
            LaunchedEffect(Unit) {
                //toast message
                mainViewModel.toastMessage.observe(lifecycleOwner) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }

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
                BackHandler(enabled = backPressedState) {
                    if(System.currentTimeMillis() - backPressedTime <= 400L) {
                        // 앱 종료
                        (context as Activity).finish()
                    } else {
                        mainViewModel.stopPlay()
                        backPressedState = true
                        Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
                val permissionState =
                    rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
                val pagerState = rememberPagerState { tabItems.size }
                var balloonResource = remember { mutableStateOf(0)}

                LaunchedEffect(Unit) {
                    textToSpeech = TextToSpeech(context) {}
                }
                SideEffect {
                    permissionState.launchPermissionRequest()
                }
//                LaunchedEffect(pagerState.currentPage) {
//                    pageName = ttsIndex(pagerState.currentPage)
//                    textToSpeech?.speak(
//                        ttsIndex(pagerState.currentPage),
//                        TextToSpeech.QUEUE_FLUSH,
//                        null,
//                        null
//                    )
//                }
                lifecycleScope.launch {
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        LaunchedEffect(pagerState.currentPage) {
                            mainViewModel.initializeTextToSpeech(context)
                            mainViewModel.startSpeak(ttsIndex(pagerState.currentPage))
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
                            Text(
                                text = ttsIndex(pagerState.currentPage), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Black,
                                modifier = Modifier.padding(20.dp)
                            )
                            IconButton(
                                onClick = {
                                    dialogViewModel.helpDialogOn()
                                    mainViewModel.startSpeak(text = mainViewModel.state.value.help_dialog)
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
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ){
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) { index ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (index == pagerState.currentPage) {
                                        when (index) {
                                            0 -> MainScreen(mainViewModel)
                                            1 -> MoneyScreen(index = index, mainViewModel)
                                            2 -> PointerScreen(index = index, mainViewModel)
                                        }
                                    }
                                    LaunchedEffect(sttValue) {
                                        //TextToSpeech?.speak(ttsIndex(pagerState.currentPage),TextToSpeech.QUEUE_FLUSH,null,null)
                                        when (sttValue) {
                                            "[텍스트]", "[text]" -> pagerState.scrollToPage(0)
                                            "[돈]", "[지폐]", "[money]", "[currency]" -> pagerState.scrollToPage(
                                                1
                                            )

                                            "[포인터]", "[pointer]" -> pagerState.scrollToPage(2)
                                        }
                                    }
                                }
                            }
                            Balloon(
                                builder = balloonBuilder,
                                onBalloonWindowInitialized = { balloonWindow = it },
                                balloonContent = {
                                    Surface(
                                        color = White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp),
                                        shadowElevation = 10.dp
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(12.dp)
                                                .verticalScroll(scrollState)
                                        ) {
                                            Text(
                                                text = showedText.value,
                                                // showedText, // 텍스트 설정
                                                color = Color.Black, // 텍스트 색상을 흰색으로 설정
                                                // '전체보기' 버튼 클릭 여부에 따라 Modifier 변경
                                                maxLines = if (isFullView) {
                                                    Int.MAX_VALUE
                                                } else {
                                                    7
                                                } // '전체보기' 버튼 클릭 여부에 따라 maxLines 변경
                                            )
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Button(onClick = {
                                                    mainViewModel.stopPlay()
                                                    mainViewModel.state.value.isButtonEnabled = true
                                                    balloonWindow?.dismiss()
                                                }) {
                                                    Text("멈추기")
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Button(onClick = { isFullView = !isFullView }) {
                                                    Text("전체보기")
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { window: BalloonWindow ->
                                // BalloonWindow에 대한 상태를 관리하는 변수 추가
                                var isBalloonShowing by remember { mutableStateOf(false) }

                                IconButton(
                                    onClick = {
                                        window.showAlignBottom()
                                    },
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(50.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    LaunchedEffect(window.balloon.isShowing) {
                                        // Balloon이 나타났거나 사라질 때마다 상태를 갱신
                                        isBalloonShowing = window.balloon.isShowing
                                    }
                                    Image(
                                        painter = if (isBalloonShowing) {
                                            painterResource(id = R.drawable.icon_top_arrow)
                                        } else {
                                            painterResource(id = R.drawable.icon_down_arrow)
                                        },
                                        contentDescription = "화살표 아이콘"
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                showedText.value = mainViewModel.state.value.detectedtext
                                mainViewModel.startSpeak(
                                    showedText.value
                                    //해석안된건 showed text
                                )
                                balloonWindow?.showAlignBottom(110, 30)
                            },
                            enabled = mainViewModel.state.value.isButtonEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, bottom = 15.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_camera2),
                                contentDescription = "TTS Button",
                                modifier = Modifier
                                        .size(100.dp)
                            )
                        }
                    }

                }
                if (dialogViewModel.isHelpDialogShown) {
                    AlertDialog(
                        onDismissRequest = dialogViewModel::onDismissHelpDialog,
                        confirmButton = {
                            Button(onClick = {
                                mainViewModel.stopPlay()
                                mainViewModel.startSpeak(text = mainViewModel.state.value.help_dialog)
                            }) {
                                Text(text = "한번 더 듣기")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    dialogViewModel.onDismissHelpDialog()
                                    mainViewModel.stopPlay()
                                }) {
                                Text(text = "나가기")
                            }
                        },
                        title = {
                            Text(
                                text = "도움말",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.help_dialog),
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    )
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

@Composable
fun getInitialPainter(): Painter {
    return getTopArrowPainter() // 초기화된 이미지로 설정
}

@Composable
fun getTopArrowPainter(): Painter {
    return painterResource(id = R.drawable.icon_top_arrow)
}

@Composable
fun getDownArrowPainter(): Painter {
    return painterResource(id = R.drawable.icon_down_arrow)
}