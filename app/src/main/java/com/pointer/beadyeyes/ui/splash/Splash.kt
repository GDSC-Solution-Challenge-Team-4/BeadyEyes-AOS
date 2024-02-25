package com.pointer.beadyeyes.ui.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pointer.beadyeyes.R
import com.pointer.beadyeyes.ui.theme.MainYellow
import com.pointer.beadyeyes.ui.theme.pretendard_light

@Composable
fun SplashScreen(modifier: Modifier=Modifier.fillMaxSize()) {
    val alpha by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2000, easing = LinearEasing),
            RepeatMode.Reverse
        ), label = ""
    )

    Surface(
        color = MainYellow, // 배경색 설정
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val logo = painterResource(id = R.drawable.icon_splash)
            Image(
                painter = logo,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(alpha),
                alignment = Alignment.Center
            )
            Text(
                text = "BEADY\nEYES",
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontFamily = pretendard_light,
                fontSize = 20.sp
            )
        }
    }
}
