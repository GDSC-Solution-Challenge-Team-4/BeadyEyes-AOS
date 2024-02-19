package de.yanneckreiss.mlkittutorial.ui.translate

data class TranslateState(
    val isButtonEnabled: Boolean = true,
    val text: String = "",
    val textToBeTranslated: String = "",
    val translatedText: String = ""
)
