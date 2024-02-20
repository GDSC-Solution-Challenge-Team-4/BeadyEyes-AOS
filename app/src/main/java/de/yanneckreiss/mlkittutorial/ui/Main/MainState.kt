package de.yanneckreiss.mlkittutorial.ui.Main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class MainState(
    var detectedtext :String = "",
    var textToSpeechInitialized :Boolean = false
)
