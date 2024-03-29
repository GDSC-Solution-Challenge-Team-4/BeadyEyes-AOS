package com.pointer.beadyeyes.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.pointer.beadyeyes.MainActivity
import com.pointer.beadyeyes.ui.theme.JetpackComposeBeadyEyesTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@OptIn(DelicateCoroutinesApi::class)
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeBeadyEyesTheme {
                SplashScreen()
            }
        }

         GlobalScope.launch(Dispatchers.Main) {
             delay(2000)
             startActivity(Intent(this@SplashActivity, com.pointer.beadyeyes.MainActivity::class.java))
             finish()

         }
    }
}
