package de.yanneckreiss.mlkittutorial.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import de.yanneckreiss.mlkittutorial.MainActivity
import de.yanneckreiss.mlkittutorial.ui.theme.JetpackComposeBeadyEyesTheme
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
             startActivity(Intent(this@SplashActivity, MainActivity::class.java))
             finish()

         }
    }
}
