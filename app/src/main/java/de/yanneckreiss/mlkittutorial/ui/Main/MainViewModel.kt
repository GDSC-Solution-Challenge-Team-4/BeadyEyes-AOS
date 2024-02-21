package de.yanneckreiss.mlkittutorial.ui.Main

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
     var textToSpeech: TextToSpeech? = null
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
        val content = text
        if (playState.isStopping() ) {
            startSpeak(content)
        } else if (playState.isWaiting()) {
            standbyIndex += lastPlayIndex
            startSpeak(content.substring(standbyIndex))
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
                    showState("TTS 객체 초기화 중 에러가 발생했습니다.")
                }
            }.apply {
                this?.setOnUtteranceProgressListener(
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

                    override fun onError(s: String) {
                        showState("재생 중 에러가 발생했습니다.")
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