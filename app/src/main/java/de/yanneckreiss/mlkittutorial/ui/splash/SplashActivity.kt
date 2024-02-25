package de.yanneckreiss.mlkittutorial.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
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


        // Splash 화면이 일정 시간 후에 자동으로 닫히고 다음 화면으로 이동하는 로직
        // 예를 들어, 3초 후에 메인 화면으로 이동하는 경우
         GlobalScope.launch(Dispatchers.Main) {
             delay(2000)
             startActivity(Intent(this@SplashActivity, MainActivity::class.java))
             finish()

         }
    }
}
