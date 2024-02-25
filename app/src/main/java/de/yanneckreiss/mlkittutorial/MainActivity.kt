package de.yanneckreiss.mlkittutorial

import android.Manifest
import android.content.Context
import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.annotations.InternalBalloonApi
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.BalloonWindow
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.overlay.BalloonOverlayAnimation
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.mlkittutorial.ui.MainScreen
import de.yanneckreiss.mlkittutorial.ui.RecordAndConvertToText
import de.yanneckreiss.mlkittutorial.ui.dialog.DialogViewModel
import de.yanneckreiss.mlkittutorial.ui.main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.money.ui.MoneyScreen
import de.yanneckreiss.mlkittutorial.ui.pointer.PointerScreen
import de.yanneckreiss.mlkittutorial.ui.splash.SplashScreen
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeBeadyEyesTheme
import de.yanneckreiss.mlkittutorial.ui.theme.MainYellow
import de.yanneckreiss.mlkittutorial.ui.theme.pretendard_bold
import de.yanneckreiss.mlkittutorial.ui.theme.pretendard_light
import de.yanneckreiss.mlkittutorial.ui.theme.pretendard_regular
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context: Context = LocalContext.current
            var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
            var sttValue by remember { mutableStateOf("") }
            val dialogViewModel: DialogViewModel = viewModel()
            val mainViewModel: MainViewModel = viewModel()

            var showedText by remember { mutableStateOf("No text detected yet..") }

            var backPressedState by remember { mutableStateOf(true) }
            var backPressedTime = 0L


            var textVisible by remember { mutableStateOf(false) }
            var isFullView by remember { mutableStateOf(false) }
            val lifecycleOwner = LocalLifecycleOwner.current
            var balloonWindow: BalloonWindow? by remember { mutableStateOf(null) }
            var isBalloonShowing by remember { mutableStateOf(false) }
            val scrollState = rememberScrollState()
            var splashToggle by remember { mutableStateOf(false) }

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
                //setArrowPosition(0.5f)
                setArrowPosition(0.91F)
                setCornerRadius(8f)
                setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                setBackgroundColorResource(R.color.white)
                setMarginHorizontal(10)
                setOverlayShape(BalloonOverlayRoundRect(12f, 12f))
                setOnBalloonDismissListener {
                    mainViewModel.stopPlay()
                    //balloonWindow?.updateSizeOfBalloonCard(250,250)
                    isBalloonShowing = false
                }
            }

            LaunchedEffect(Unit) {
                delay(3000)
                mainViewModel.toastMessage.observe(lifecycleOwner) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                textToSpeech = TextToSpeech(context) {}
            }
            LaunchedEffect(mainViewModel.state) {
                showedText = mainViewModel.state.value.detectedtext
            }
            SideEffect {
                permissionState.launchPermissionRequest()
            }
            JetpackComposeBeadyEyesTheme {
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
                    if (System.currentTimeMillis() - backPressedTime <= 400L) {
                        // 앱 종료
                        (context as Activity).finish()
                    } else {
                        mainViewModel.stopPlay()
                        backPressedState = true
                        Toast.makeText(context, "한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
                val pagerState = rememberPagerState { tabItems.size }
                LaunchedEffect(pagerState.currentPage) {
                    mainViewModel.initializeTextToSpeech(context)
                    mainViewModel.startSpeak(ttsIndex(pagerState.currentPage))
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
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
                                text = ttsIndex(pagerState.currentPage),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(bottom=10.dp)
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
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
                                        when (sttValue) {
                                            "[텍스트]", "[text]" -> pagerState.scrollToPage(0)
                                            "[돈]", "[지폐]", "[money]", "[currency]" -> pagerState.scrollToPage(1)
                                            "[포인터]", "[pointer]" -> pagerState.scrollToPage(2)
                                        }
                                    }
                                }
                            }
                            Balloon(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopEnd)
                                    .padding(end = 5.dp),
                                builder = balloonBuilder,
                                onBalloonWindowInitialized = { balloonWindow = it },
                                balloonContent = {
                                    Surface(
                                        color = White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp),
                                        shadowElevation = 20.dp
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .background(White)
                                                .padding(12.dp)
                                                .verticalScroll(scrollState)
                                        ) {
                                            Text(
                                                text = showedText,
                                                modifier = Modifier.fillMaxSize(),
                                                color = Black,
                                                maxLines = if (isFullView) {
                                                    Int.MAX_VALUE
                                                } else {
                                                    7
                                                }
                                            )
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    //.weight(1F)
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                Button(
                                                    onClick = {
                                                        mainViewModel.stopPlay()
                                                        mainViewModel.state.value.isButtonEnabled =
                                                            true
                                                        balloonWindow?.dismiss()
                                                    },
                                                    colors=buttonColors(MainYellow)
                                                ) {
                                                    Text("멈추기")
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Button(
                                                    onClick = { isFullView = !isFullView },
                                                    colors=buttonColors(MainYellow)
                                                ) {
                                                    Text("전체보기")
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { window: BalloonWindow ->
                                IconButton(
                                    onClick = {
                                        isBalloonShowing = true
                                        window.showAlignBottom()
                                    },
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(50.dp)
                                        .align(Alignment.TopEnd)
                                ) {
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
                                showedText = mainViewModel.state.value.detectedtext
                                mainViewModel.startSpeak(showedText)
                                isBalloonShowing = true
                                balloonWindow?.showAlignBottom(110, 30)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 15.dp, bottom = 15.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_bottom_tts_png),
                                contentDescription = "TTS Button",
                                modifier = Modifier
                                    .size(70.dp)
                            )
                        }
                    }

                }
                if (dialogViewModel.isHelpDialogShown) {
                    AlertDialog(
                        shape = RoundedCornerShape(15.dp),
                        icon= {painterResource(id = R.drawable.icon_main_logo)},
                        onDismissRequest =
                            dialogViewModel::onDismissHelpDialog,
                        confirmButton = {
                            Button(
                                onClick = {
                                    mainViewModel.stopPlay()
                                    mainViewModel.startSpeak(text = mainViewModel.state.value.help_dialog)
                                },
                                colors=buttonColors(MainYellow)
                            ) {
                                Text(text = "한번 더 듣기")
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxHeight()
                            .padding(start = 10.dp, top = 80.dp, bottom = 80.dp, end = 10.dp),
                        dismissButton = {
                            Button(
                                onClick = {
                                    dialogViewModel.onDismissHelpDialog()
                                    mainViewModel.stopPlay()
                                },
                                colors=buttonColors(MainYellow)
                            ) {
                                Text(text = "나가기")
                            }
                        },
                        title = {
                            Text(
                                text = "How to use",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontFamily = pretendard_bold
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.help_dialog),
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                fontFamily = pretendard_light
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