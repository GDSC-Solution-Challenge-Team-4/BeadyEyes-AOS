package de.yanneckreiss.mlkittutorial.translate

data class TranslateState(
    val isButtonEnabled: Boolean = true,
    val text: String = "",
    val textToBeTranslated: String = "",
    val translatedText: String = ""
)
