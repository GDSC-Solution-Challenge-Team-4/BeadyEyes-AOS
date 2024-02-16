package de.yanneckreiss.mlkittutorial.ui.navigate

import de.yanneckreiss.cameraxtutorial.R

sealed class BottomNavItem(
    val title: Int, val icon: Int, val screeenRoute: String
){
    object Ocr : BottomNavItem(R.string.text_OCR, R.drawable.icon_ocr, "OCR")
    object Pointer : BottomNavItem(R.string.text_pointer, R.drawable.icon_pointer, "POINTER")
    object Money : BottomNavItem(R.string.text_money, R.drawable.icon_translate, "MONEY")
}
