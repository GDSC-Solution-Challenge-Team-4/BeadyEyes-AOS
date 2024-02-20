package de.yanneckreiss.mlkittutorial.ui.translate


import android.content.ContentValues.TAG
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

 class TranslateViewModel : ViewModel() {
    private val _state = mutableStateOf(TranslateState())
    val state: State<TranslateState> = _state

    private var textToSpeech: TextToSpeech? = null
    var textLanguage: String = ""

    val languageIdentifier = LanguageIdentification.getClient()

    //create a function which will update
    // value for text in our MainScreenState
    // when the value in the text field changes

    private fun onTextFiledValueChange(text: String) {
        _state.value = state.value.copy(
            text = text
        )
    }

    private fun textToSpeech(context: Context) {
        //disable button when function start

        textToSpeech = TextToSpeech(
            context
        ) {
            //this lamda block provide us code
            //if successfull or error
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.getDefault()
                    //speed of audio
                    txtToSpeech.setSpeechRate(1.0f)
                    txtToSpeech.speak(
                        _state.value.text,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                    //this will produce sound now enable the button
                }
            }

            textToSpeech?.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _state.value = _state.value.copy(
                            isButtonEnabled = false
                        )
                    }

                    override fun onDone(utteranceId: String?) {
                        _state.value = _state.value.copy(
                            isButtonEnabled = true
                        )
                    }

                    override fun onError(utteranceId: String?) {
                    }

                }
            )
        }

    }
     fun OnlytextToSpeech(
         context: Context,
         text: String
     ) {
         //disable button when function start

         textToSpeech = TextToSpeech(
             context
         ) {
             //this lamda block provide us code
             //if successfull or error
             if (it == TextToSpeech.SUCCESS) {
                 textToSpeech?.let { txtToSpeech ->
                     txtToSpeech.language = Locale.getDefault()
                     //speed of audio
                     txtToSpeech.setSpeechRate(1.0f)
                     txtToSpeech.speak(
                         text,
                         TextToSpeech.QUEUE_ADD,
                         null,
                         null
                     )
                     //this will produce sound now enable the button
                 }
             }

             textToSpeech?.setOnUtteranceProgressListener(
                 object : UtteranceProgressListener() {
                     override fun onStart(utteranceId: String?) {
                         _state.value = _state.value.copy(
                             isButtonEnabled = false
                         )
                     }

                     override fun onDone(utteranceId: String?) {
                         _state.value = _state.value.copy(
                             isButtonEnabled = true
                         )
                     }

                     override fun onError(utteranceId: String?) {
                         Log.e("Error","에러가 발생했습니다${it.toString()}")
                     }

                 }
             )
         }

     }

    fun onTextToBeTranslatedChange(text: String) {
        _state.value = state.value.copy(
            textToBeTranslated = text
        )
    }

    fun onTranslateButtonClick(
        text: String,
        context: Context
    ) {
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i(TAG, "Can't identify language.")
                } else {
                    Log.i(TAG, "Language: $languageCode")
                    textLanguage = languageCode.substring(0, 2)
                }
                //here text will be text to be translated
                //and context for showing toast messages
                val options = TranslatorOptions
                    .Builder().setSourceLanguage(textLanguage)
                    .setTargetLanguage(Locale.getDefault().language).build()

                val languageTranslator = Translation.getClient(options)

                languageTranslator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        _state.value = state.value.copy(
                            translatedText = translatedText
                        )
                        onTextFiledValueChange(state.value.translatedText)
                        textToSpeech(context)
                    }
                    .addOnFailureListener {
                        Log.e("error", it.toString())
                        Toast.makeText(
                            context,
                            "Download started..",
                            Toast.LENGTH_SHORT
                        ).show()
                        downloadModaelIfNotAvailable(languageTranslator, context)
                    }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
                Log.e("error 1", it.toString())
            }
    }

    private fun downloadModaelIfNotAvailable(
        languageTranlator: Translator,
        context: Context
    ) {
        //while downloading lets disable the button
        _state.value = _state.value.copy(
            isButtonEnabled = false
        )

        val conditions = DownloadConditions
            .Builder()
            .requireWifi()
            .build()

        languageTranlator
            .downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Download Succesfully",
                    Toast.LENGTH_SHORT
                ).show()
                _state.value = _state.value.copy(
                    isButtonEnabled = true
                )
            }
            .addOnFailureListener {
                Log.e("error", it.toString())
                Toast.makeText(
                    context,
                    "Couldn't download the models",
                    Toast.LENGTH_SHORT
                ).show()
                _state.value = _state.value.copy(
                    isButtonEnabled = true
                )
            }
    }


}

