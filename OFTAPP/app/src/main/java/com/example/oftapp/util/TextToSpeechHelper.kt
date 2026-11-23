package com.example.oftapp.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    var ttsHabilitado: Boolean = true

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "CL"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }
            isInitialized = true
        } else {
            Log.e("TextToSpeechHelper", "Error al inicializar TextToSpeech nativo.")
        }
    }

    /**
     * Reproduce el dictado por voz SOLO si [activado] y [ttsHabilitado] son verdaderos.
     */
    fun hablar(texto: String, activado: Boolean = ttsHabilitado) {
        if (activado && isInitialized && texto.isNotBlank()) {
            tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "OFTAPP_TTS_ID")
        }
    }

    fun detener() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
    }

    fun shutdown() {
        detener()
        tts?.shutdown()
        tts = null
    }
}
