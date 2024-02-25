@file:OptIn(ExperimentalPermissionsApi::class)

package com.pointer.beadyeyes.ui.camera

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pointer.beadyeyes.ui.main.MainViewModel
import com.pointer.beadyeyes.ui.no_permission.NoPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
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