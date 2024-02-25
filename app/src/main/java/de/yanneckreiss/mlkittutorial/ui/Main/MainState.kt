package de.yanneckreiss.mlkittutorial.ui.Main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class MainState(
    var detectedtext :String = "",
    var textToSpeechInitialized :Boolean = false,
    var isButtonEnabled : Boolean = true,
    var help_dialog :String = "Beady Eye는 저시력자 및 시각장애인 분들이 문자와 지폐를 쉽게 인식할 수 있도록 지원하는 앱입니다." +
            "\n" +
            "주요 기능들\n" +
            "\n" +
            "실시간 문자인식: 카메라를 통해 원하는 문자를 찍으면, 앱이 그 문자를 인식하여 실시간으로 번역하고 읽어줍니다. 이 기능은 메뉴, 책, 물품 라벨 등 다양한 종류의 텍스트에 사용할 수 있습니다.\n" +
            "지폐 인식: 저시력자 분들이 정확하게 지폐를 인식하기 어려울 때 이 기능을 사용하면 앱이 카메라를 통해 찍힌 지폐를 인식하고 해당 지폐의 금액을 알려줍니다.\n" +
            "Pointer 기능: 카메라를 통해 화면 상에서 손가락으로 가리킨 특정 부분만을 찍으면, 해당 부분의 문자를 인식하여 실시간으로 번역하고 읽어줍니다. 이 기능은 특정 부분의 텍스트만을 집중적으로 이해하고 싶을 때 유"
)
