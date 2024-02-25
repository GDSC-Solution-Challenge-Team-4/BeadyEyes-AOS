package com.pointer.beadyeyes.ui.money

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.pointer.beadyeyes.ui.main.MainViewModel
import com.pointer.beadyeyes.ui.theme.JetpackComposeBeadyEyesTheme

@Composable
fun MoneyScreen(index : Int, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context: Context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CameraContentMoney(
            context = context,
            index = index,
            onResult = {
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