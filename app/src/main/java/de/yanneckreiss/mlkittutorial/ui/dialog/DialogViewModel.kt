package de.yanneckreiss.mlkittutorial.ui.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {

    var isHelpDialogShown by mutableStateOf(false)
        private set


    fun helpDialogOn(){
        isHelpDialogShown = true
    }

    fun onDismissHelpDialog(){
        isHelpDialogShown = false
    }
}