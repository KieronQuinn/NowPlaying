package com.kieronquinn.app.pixelambientmusic.utils

import android.hardware.SensorPrivacyManager.Sensors
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.kieronquinn.app.ambientmusicmod.IMicrophoneDisabledStateCallback

@RequiresApi(Build.VERSION_CODES.S)
class SensorPrivacyListenerWrapper(private val original: Any): IMicrophoneDisabledStateCallback.Stub() {

    companion object {
        val SENSOR_PRIVACY_LISTENER_CLASS by lazy {
            Class.forName(
                "android.hardware.SensorPrivacyManager\$OnSensorPrivacyChangedListener"
            )
        }
    }

    private fun onSensorPrivacyChanged(sensor: Int, enabled: Boolean) {
        SENSOR_PRIVACY_LISTENER_CLASS.getMethod(
            "onSensorPrivacyChanged", Integer.TYPE, Boolean::class.java
        ).invoke(original, sensor, enabled)
    }

    override fun onMicrophoneDisabledStateChanged(disabled: Boolean) {
        onSensorPrivacyChanged(Sensors.MICROPHONE, disabled)
    }

}