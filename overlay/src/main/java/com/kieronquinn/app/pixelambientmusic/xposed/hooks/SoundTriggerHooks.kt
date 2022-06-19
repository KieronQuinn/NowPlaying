package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.ComponentName
import android.content.Context
import android.hardware.soundtrigger.SoundTrigger
import android.media.soundtrigger.SoundTriggerManager
import android.os.Bundle
import com.android.internal.app.ISoundTriggerService
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.util.*


/**
 *  Hook system calls to [SoundTriggerManager] and prevent them from proceeding, effectively
 *  neutering the class and allowing us to manually trigger 'recognition' events without needing
 *  the MANAGE_SOUND_TRIGGER permission.
 */
class SoundTriggerHooks : XposedHooks() {

    override val clazz = SoundTriggerManager::class.java

    private fun constructor_main(context: Context, trigger: ISoundTriggerService) = MethodHook {
        //Prevent init so system calls are never made
        MethodResult.Replace<Void>(null)
    }

    private fun isRecognitionActive(uuid: UUID) = MethodHook {
        //Prevent system call as service is not there
        MethodResult.Replace(false)
    }

    private fun startRecognition(
        uuid: UUID,
        bundle: Bundle,
        component: ComponentName,
        config: SoundTrigger.RecognitionConfig
    ) = MethodHook {
        //Prevent system call as service is not there
        MethodResult.Replace(0)
    }

    private fun deleteModel(uuid: UUID) = MethodHook {
        //Prevent system call as service is not there
        MethodResult.Replace<Void>(null)
    }

    private fun loadSoundModel(model: SoundTrigger.SoundModel) = MethodHook {
        //Prevent system call as service is not there
        MethodResult.Replace(0)
    }

    private fun getModelState(soundModelId: UUID) = MethodHook {
        //Prevent system call as service is not there
        MethodResult.Replace(0)
    }

}