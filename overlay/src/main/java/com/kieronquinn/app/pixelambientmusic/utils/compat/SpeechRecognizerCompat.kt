package com.kieronquinn.app.pixelambientmusic.utils.compat

import android.content.Context
import android.os.Build
import android.speech.SpeechRecognizer

object SpeechRecognizerCompat {

    @JvmStatic
    fun isOnDeviceRecognitionAvailable(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
        } else false
    }

}