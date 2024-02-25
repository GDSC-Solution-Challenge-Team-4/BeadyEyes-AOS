package de.yanneckreiss.mlkittutorial.ui.money.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import de.yanneckreiss.mlkittutorial.ui.main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeBeadyEyesTheme

@Composable
fun MoneyScreen(index : Int, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current
    //val detectedText = remember { mutableStateOf("No text detected yet..") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CameraContentMoney(context = context, index = index,
            onResult = {
                //detectedText.value = it
                //Log.d("돈",it)
                mainViewModel.onTextValueChange(it)
        })
    }

}

@Preview(showBackground = true)
@Composable
fun MoneyPreview() {
    JetpackComposeBeadyEyesTheme {
        //MoneyScreen()
    }
}