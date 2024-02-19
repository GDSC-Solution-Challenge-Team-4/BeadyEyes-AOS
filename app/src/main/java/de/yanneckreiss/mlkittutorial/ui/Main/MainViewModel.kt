package de.yanneckreiss.mlkittutorial.ui.Main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel :ViewModel() {
    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

     fun onTextValueChange(text: String) {
        _state.value = state.value.copy(
            detectedtext = text
        )
    }
}