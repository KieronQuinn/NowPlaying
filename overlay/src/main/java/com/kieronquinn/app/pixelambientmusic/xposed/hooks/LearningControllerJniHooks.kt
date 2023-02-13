package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.google.knowledge.cerebra.sense.textclassifier.tclib.TextClassifierLib
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Blocks native methods, faking results
 */
class LearningControllerJniHooks: XposedHooks() {

    override val clazz: Class<*> = 
        Class.forName("com.google.android.apps.miphone.aiai.fedass.learning.impl.LearningControllerJni")

    fun nativeClearLearnedCorrectionsInMemory() = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeGetLearnedCorrections() = MethodHook {
        MethodResult.Replace(byteArrayOf())
    }

    fun nativeLearn(a: TextClassifierLib?) = MethodHook {
        MethodResult.Replace(byteArrayOf())
    }

    fun nativeLoadPersistedCorrections() = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeOnInputContextSnapshot(a: ByteArray?) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeSetConfig(a: ByteArray?) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeSetIsLearnableSession(a: Boolean) = MethodHook {
        MethodResult.Replace(Unit)
    }

    fun nativeSetPersonalizationOptIn(a: Boolean) = MethodHook {
        MethodResult.Replace(Unit)
    }

}