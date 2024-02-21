package de.yanneckreiss.mlkittutorial

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.cameraxtutorial.R
import de.yanneckreiss.mlkittutorial.ui.Main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.MainScreen
import de.yanneckreiss.mlkittutorial.ui.RecordAndConvertToText
import de.yanneckreiss.mlkittutorial.ui.dialog.DialogViewModel
import de.yanneckreiss.mlkittutorial.ui.money.ui.MoneyScreen
import de.yanneckreiss.mlkittutorial.ui.pointer.PointerScreen
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeMLKitTutorialTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val context = LocalContext.current

            var sttValue by remember { mutableStateOf("") }
            val dialogViewModel: DialogViewModel = viewModel()
            val mainViewModel: MainViewModel = viewModel()


            var showedText = "not found yet"

            var textVisible by remember { mutableStateOf(false) }
            var isFullView by remember { mutableStateOf(false) }

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

                        val pagerState = rememberPagerState {
                            tabItems.size
                        }
                        LaunchedEffect(pagerState.currentPage) {
                            mainViewModel.initializeTextToSpeech(context)
                            mainViewModel.startPlay(ttsIndex(pagerState.currentPage))
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
                            IconButton(
                                onClick = {
                                    dialogViewModel.helpDialogOn()
                                   mainViewModel.startPlay("도움말")
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
                        if (textVisible) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(10.dp), // 끝이 둥근 모양을 만들기 위해 RoundedCornerShape 사용
                                    color = Color.Gray, // 박스의 배경색을 회색으로 설정
                                    modifier = Modifier.padding(16.dp).width(100.dp) // 박스 주변에 패딩 추가
                                ) {
                                    Text(
                                        text = showedText, // 텍스트 설정
                                        color = Color.White, // 텍스트 색상을 흰색으로 설정
                                        modifier = if (isFullView) Modifier.verticalScroll(
                                            rememberScrollState()
                                        ) else Modifier.padding(16.dp), // '전체보기' 버튼 클릭 여부에 따라 Modifier 변경
                                        maxLines = if (isFullView) Int.MAX_VALUE else 3, // '전체보기' 버튼 클릭 여부에 따라 maxLines 변경
                                        overflow = if (isFullView) TextOverflow.Visible else TextOverflow.Ellipsis // '전체보기' 버튼 클릭 여부에 따라 overflow 변경
                                    )

                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.End // 버튼을 오른쪽에 정렬
                                ) {
                                    // '멈추기' 버튼
                                    Button(onClick = {
                                        !textVisible
                                        mainViewModel.stopPlay()
                                    }) {
                                        Text("나가기")
                                    }
                                    Spacer(Modifier.width(8.dp)) // 버튼 사이에 간격 추가
                                    // '전체보기' 버튼
                                    Button(onClick = { !isFullView }) {
                                        Text("전체보기")
                                    }
                                }
                            }
                        }
                        IconButton(
                            onClick = { textVisible = !textVisible },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                painter = if (textVisible) painterResource(id = R.drawable.icon_hidearrow) else painterResource(
                                    id = R.drawable.icon_showarrow
                                ),
                                contentDescription = "화살표 아이콘",
                                tint = Color.Black
                            )
                        }
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) { index ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                when (index) {
                                    0 -> MoneyScreen()
                                    1 -> MainScreen(mainViewModel)
                                    2 -> PointerScreen()
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    showedText = mainViewModel.state.value.detectedtext
                                    mainViewModel.startPlay(
                                        showedText
                                    )
                                    dialogViewModel.shortDialogOn()

                                }, enabled = mainViewModel.state.value.isButtonEnabled,
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
//                if (dialogViewModel.isShortDialogShown) {
//                    AlertDialog(onDismissRequest = {
//                        dialogViewModel.onDismissShortDialog()
//                        mainViewModel.stopPlay()
//                    }, confirmButton = {
//                        Button(onClick = {
//                            dialogViewModel.onDismissShortDialog()
//                            dialogViewModel.fullDialogOn()
//                        }) {
//                            Text(text = "확대")
//                        }
//                    }, dismissButton = {
//                        Button(onClick = {
//                            dialogViewModel.onDismissShortDialog()
//                            mainViewModel.stopPlay()
//                        }) {
//                            Text(text = "나가기")
//                        }
//                    }, title = {
//                        Text(text = "감지된 문자")
//                    }, text = {
//                        Text(
//                            text = showedText,
//                            maxLines = 3,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//
//                    )
//
//                } // 3줄짜리 작은 text dialog
//                if (dialogViewModel.isFullDialogShown) {
//                    AlertDialog(onDismissRequest = {
//                        dialogViewModel.onDismissFullDialog()
//                        mainViewModel.stopPlay()
//                    }, confirmButton = {
//                        Button(onClick = {
//                            mainViewModel.clearAll()
//                            mainViewModel.startPlay(showedText)
//                        }
//                        ) {
//                            Text(text = " 한번 더 읽기")
//                        }
//                    }, dismissButton = {
//                        Button(onClick = {
//                            dialogViewModel.onDismissFullDialog()
//                            mainViewModel.stopPlay()
//                        }) {
//                            Text(text = "나가기")
//                        }
//                    }, title = {
//                        Text(text = "감지된 문자")
//                    }, text = {
//                        Text(
//                            text = showedText,
//                            Modifier.verticalScroll(rememberScrollState())
//                        )
//                    })
//                }// 스크롤 되는 text dialog
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
                //toast message
                mainViewModel.toastMessage.observe(this) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
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

