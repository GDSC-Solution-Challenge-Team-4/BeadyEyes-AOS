package com.pointer.beadyeyes.ui.main

data class MainState(
    var detectedtext :String = "Hold On Please, we are waiting response.",
    var textToSpeechInitialized :Boolean = false,
    var isButtonEnabled : Boolean = true,
    var help_dialog :String = "\"Beady Eyes\" is an app designed to assist individuals with low vision and visual impairments in easily recognizing text and currency.\n" +
            "\n" +
            "Key Features:\n" +
            "\n" +
            "Real-time Text Recognition: By capturing desired text through the camera, the app recognizes and translates it in real-time, providing an audible output. This feature can be utilized for various types of text such as menus, books, product labels, and more.\n" +
            "\n" +
            "Currency Recognition: When individuals with low vision find it challenging to accurately identify currency notes, this feature comes to their aid. The app uses the camera to recognize the captured currency and informs the user about the denomination.\n" +
            "\n" +
            "Pointer Functionality: Using the camera, users can point at a specific area on the screen with their finger, and the app will recognize and translate the text in that particular area in real-time. This feature is useful when users want to focus specifically on certain parts of text."
        )
