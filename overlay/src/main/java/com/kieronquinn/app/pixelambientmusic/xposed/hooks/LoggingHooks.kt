package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.util.Log
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser.SkippedReason
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.model.RecognitionMetadata
import com.kieronquinn.app.pixelambientmusic.model.RecognitionSource
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.lang.reflect.Modifier

/**
 *  Logging is stripped from the APK, for debugging and to extract the remaining time of the song,
 *  as well as reasons for skipping recognition (eg. music playing or a call in progress)
 *  we need to find the class. This can be done by using a stable class that uses logging -
 *  LevelDbTable is great for this since it also only has one static final field (the logger) -
 *  and then traverse to the logging class. We can then use this to map known methods to hooks.
 *
 *  any_ methods are used here as the names are obfuscated and may change, but the arguments will
 *  not. They automatically get mapped to methods in the class.
 */
class LoggingHooks : XposedHooks() {

    companion object {
        private const val TAG = "PixAmbMusic"
        private const val MESSAGE_OFFSET =
            "Assumed offset position of current match: %s, estimated remaining time: %s."
        private const val MESSAGE_SKIPPED_AUDIO =
            "Skipping on-device song recognition due to audio playback %s"
        private const val MESSAGE_SKIPPED_PHONE_CALL =
            "Skipping recognition due to ongoing phone call"
        private const val MESSAGE_SKIPPED_SYSTEM_USER =
            "Skipping recognition. System user not in foreground."

        /**
         *  Same method as in [InjectionHooks.findInjectionClass], get the injector and then the
         *  logger is its sole `final static` field.
         */
        private fun findLoggingClass(): Class<*>? {
            val levelDbTable =
                Class.forName("com.google.intelligence.sense.leveldb.LevelDbTable")
            val injector = levelDbTable.declaredFields.firstOrNull {
                Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers)
            } ?: return null
            val logger = injector.type.declaredFields.firstOrNull {
                Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers)
            } ?: return null
            return logger.type
        }
    }

    override val clazz = findLoggingClass()!!

    private fun log(builder: () -> String) {
        if(!DeviceConfigOverrides.isLoggingEnabled()) return
        Log.d(TAG, builder())
    }

    private fun any_A(message: String, format1: Int, format2: Any?) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_B(message: String, format1: Int, format2: Boolean) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_C(message: String, format1: Long, format2: Int) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_D(message: String, format1: Long, format2: Long) = MethodHook {
        if(message == MESSAGE_OFFSET){
            MusicRecogniser.onMetadataCalculated(
                RecognitionMetadata(
                    System.currentTimeMillis(),
                    format1,
                    format2
                )
            )
        }
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_E(message: String, format1: Long, format2: Any?) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_F(message: String, format1: Long, format2: Boolean) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_G(message: String, format1: Any?, format2: Double) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_H(message: String, format1: Any?, format2: Float) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_I(message: String, format1: Any?, format2: Int) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_J(message: String, format1: Any?, format2: Long) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_K(message: String, format1: Any?, format2: Any?) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_L(message: String, format1: Any?, format2: Boolean) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_M(message: String, format1: Boolean, format2: Int) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_N(message: String, format1: Boolean, format2: Any?) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_O(message: String, format1: Boolean, format2: Boolean) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_P(message: String, format1: Any?, format2: Any?, format3: Any?) = MethodHook {
        log { String.format(message, format1, format2, format3) }
        MethodResult.Skip<Void>()
    }

    private fun any_Q(message: String, format1: Any?, format2: Any?, format3: Any?, format4: Any?) =
        MethodHook {
            log { String.format(message, format1, format2, format3, format4) }
            MethodResult.Skip<Void>()
        }

    private fun any_R(
        message: String,
        format1: Any?,
        format2: Any?,
        format3: Any?,
        format4: Any?,
        format5: Any?
    ) = MethodHook {
        log { String.format(message, format1, format2, format3, format4, format5) }
        MethodResult.Skip<Void>()
    }

    private fun any_S(
        message: String,
        format1: Any?,
        format2: Any?,
        format3: Any?,
        format4: Any?,
        format5: Any?,
        format6: Any?
    ) = MethodHook {
        log { String.format(message, format1, format2, format3, format4, format5, format6) }
        MethodResult.Skip<Void>()
    }

    private fun any_T(
        message: String,
        format1: Any?,
        format2: Any?,
        format3: Any?,
        format4: Any?,
        format5: Any?,
        format6: Any?,
        format7: Any?
    ) {
        log {
            String.format(message, format1, format2, format3, format4, format5, format6, format7)
        }
        MethodResult.Skip<Void>()
    }

    private fun any_U(
        message: String,
        format1: Any?,
        format2: Any?,
        format3: Any?,
        format4: Any?,
        format5: Any?,
        format6: Any?,
        format7: Any?,
        format8: Any?
    ) {
        log {
            String.format(
                message,
                format1,
                format2,
                format3,
                format4,
                format5,
                format6,
                format7,
                format8
            )
        }
        MethodResult.Skip<Void>()
    }

    private fun any_V(
        message: String,
        format1: Any?,
        format2: Any?,
        format3: Any?,
        format4: Any?,
        format5: Any?,
        format6: Any?,
        format7: Any?,
        format8: Any?,
        format9: Any?,
        format10: Any?
    ) {
        log {
            String.format(
                message,
                format1,
                format2,
                format3,
                format4,
                format5,
                format6,
                format7,
                format8,
                format9,
                format10
            )
        }
        MethodResult.Skip<Void>()
    }

    private fun any_W(message: String, format1: Array<Any?>) = MethodHook {
        log { String.format(message, *format1) }
        MethodResult.Skip<Void>()
    }

    private fun any_s(message: String) = MethodHook {
        when(message) {
            MESSAGE_SKIPPED_PHONE_CALL -> {
                MusicRecogniser.onRecognitionSkipped(SkippedReason.ON_CALL, RecognitionSource.NNFP)
            }
            MESSAGE_SKIPPED_SYSTEM_USER -> {
                MusicRecogniser.onRecognitionSkipped(
                    SkippedReason.SYSTEM_USER_NOT_IN_FOREGROUND, RecognitionSource.NNFP
                )
            }
        }
        log { message }
        MethodResult.Skip<Void>()
    }

    private fun any_t(message: String, format1: Int) = MethodHook {
        log { String.format(message, format1) }
        MethodResult.Skip<Void>()
    }

    private fun any_u(message: String, format1: Long) = MethodHook {
        log { String.format(message, format1) }
        MethodResult.Skip<Void>()
    }

    private fun any_v(message: String, format1: Any?) = MethodHook {
        if(message == MESSAGE_SKIPPED_AUDIO) {
            MusicRecogniser.onRecognitionSkipped(SkippedReason.MUSIC, RecognitionSource.NNFP)
        }
        log { String.format(message, format1) }
        MethodResult.Skip<Void>()
    }

    private fun any_w(message: String, format1: Double, format2: Any?) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_x(message: String, format1: Float, format2: Int) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_y(message: String, format1: Int, format2: Int) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

    private fun any_z(message: String, format1: Int, format2: Long) = MethodHook {
        log { String.format(message, format1, format2) }
        MethodResult.Skip<Void>()
    }

}