@file:OptIn(ExperimentalPermissionsApi::class)

package de.yanneckreiss.mlkittutorial.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.mlkittutorial.ui.Main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.camera.CameraScreen
import de.yanneckreiss.mlkittutorial.ui.no_permission.NoPermissionScreen

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

            MainContent(
            hasPermission = cameraPermissionState.status.isGranted,
            onRequestPermission = cameraPermissionState::launchPermissionRequest,
                text = mainViewModel.state.value.detectedtext,
                onTextValueChange = { text -> mainViewModel.onTextValueChange(text) }

            )



}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    text:String,
    onTextValueChange :(String) -> Unit
) {

    if (hasPermission) {
        CameraScreen(text,onTextValueChange)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}