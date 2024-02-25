@file:OptIn(ExperimentalPermissionsApi::class)

package de.yanneckreiss.mlkittutorial.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.yanneckreiss.mlkittutorial.ui.main.MainViewModel
import de.yanneckreiss.mlkittutorial.ui.camera.CameraScreen
import de.yanneckreiss.mlkittutorial.ui.no_permission.NoPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun MainScreen(mainViewModel: MainViewModel) {

    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    MainContent(
        hasPermission = cameraPermissionState.status.isGranted,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,
        onTextValueChange = { mainViewModel.onTextValueChange(it) }
    )

}

@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onTextValueChange :(String) -> Unit
) {
    if (hasPermission) {
        CameraScreen(onTextValueChange = onTextValueChange )
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}