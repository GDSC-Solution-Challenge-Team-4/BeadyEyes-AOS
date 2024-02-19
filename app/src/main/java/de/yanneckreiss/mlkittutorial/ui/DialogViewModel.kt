package de.yanneckreiss.mlkittutorial.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {
    var isShortDialogShown by mutableStateOf(false)
        private set
    var isFullDialogShown by mutableStateOf(false)
        private set

    fun shortDialogOn(){
        isShortDialogShown = true
    }

    fun onDismissShortDialog(){
        isShortDialogShown = false
    }
    fun fullDialogOn(){
        isFullDialogShown = true
    }

    fun onDismissFullDialog(){
        isFullDialogShown = false
    }
}