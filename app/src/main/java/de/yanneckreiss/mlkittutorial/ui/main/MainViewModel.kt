package de.yanneckreiss.mlkittutorial.ui.main

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.yanneckreiss.mlkittutorial.ui.tts.PlayState
import java.util.Locale

class MainViewModel : ViewModel() {
    private val _state = mutableStateOf(MainState())
    val state: State<MainState> = _state

    //tts
    private var textToSpeech: TextToSpeech? = null
    private var playState = PlayState.STOP
    private  var standbyIndex = 0
    private  var lastPlayIndex = 0

    val toastMessage = MutableLiveData<String>()

    fun onTextValueChange(text: String) {
        _state.value = state.value.copy(
            detectedtext = text
        )
    }


    fun showState(msg: String) {
        toastMessage.value = msg
    }

    fun startSpeak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, text)
    }

    fun clearAll() {
        playState = PlayState.STOP
        standbyIndex = 0
        lastPlayIndex = 0


    }

    fun startPlay(text: String) {
        if (playState.isStopping() ) {
            startSpeak(text)
        } else if (playState.isWaiting()) {
            standbyIndex += lastPlayIndex
            startSpeak(text.substring(standbyIndex))
        }
        playState = PlayState.PLAY
    }

    fun pausePlay() {
        if (playState.isPlaying()) {
            playState = PlayState.WAIT
            textToSpeech?.stop()
        }
    }

    fun stopPlay() {
        textToSpeech?.stop()
        clearAll()
    }


    fun initializeTextToSpeech(context: Context) {


        if (!state.value.textToSpeechInitialized) {
            textToSpeech = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech?.language = Locale.getDefault()
                    _state.value = state.value.copy(
                        textToSpeechInitialized = true
                    )

                } else {
                    showState("An error occurred during TTS object initialization.")
                }
            }.apply {
                this.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(s: String) {
                            _state.value = _state.value.copy(
                                isButtonEnabled = false
                            )
                        }

                        override fun onDone(s: String) {
                            _state.value = _state.value.copy(
                                isButtonEnabled = true
                            )
                            clearAll()
                        }

                        @Deprecated("Deprecated in Java",
                            ReplaceWith("showState(\"An error occurred during playback.\")")
                        )
                        override fun onError(s: String) {
                            showState("An error occurred during playback.")
                        }

                        override fun onRangeStart(
                            utteranceId: String, start: Int, end: Int, frame: Int
                        ) {
                            lastPlayIndex = start
                        }
                    })
            }
        }
    }


}