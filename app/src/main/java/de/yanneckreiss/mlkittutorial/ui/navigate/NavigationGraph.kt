package de.yanneckreiss.mlkittutorial.ui.navigate


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.yanneckreiss.mlkittutorial.ui.camera.CameraScreen

@Composable
fun NavigationGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Ocr.screeenRoute
    ){
        composable(route = BottomNavItem.Ocr.screeenRoute){
            CameraScreen()
        }
        composable(route = BottomNavItem.Pointer.screeenRoute){
            PointerScreen()
        }
        composable(route = BottomNavItem.Money.screeenRoute){
            CameraScreen()
        }
    }
}