package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.app.Notification
import com.kieronquinn.app.pixelambientmusic.components.recogniser.MusicRecogniser
import com.kieronquinn.app.pixelambientmusic.utils.extensions.getChannelId
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  On Pixels, Ambient Music runs multiple times during a track to see if it's still playing,
 *  making use of the low power DSP. We don't do that, to save battery. Instead, this class
 *  intercepts the Now Playing song notifications and extends their expiry time to the end of the
 *  song, if it is available.
 */
class NotificationHooks: XposedHooks() {

    companion object {
        private const val MUSIC_CHANNEL_ID =
            "com.google.intelligence.sense.ambientmusic.MusicNotificationChannel"
    }

    override val clazz = Notification.Builder::class.java

    private fun setContentText(text: CharSequence?) = MethodHook(afterHookedMethod = {
        val builder = thisObject as Notification.Builder
        if(builder.getChannelId() == MUSIC_CHANNEL_ID && text?.isNotEmpty() == true){
            MusicRecogniser.getExpireTime()?.let {
                builder.setTimeoutAfter(it)
            }
        }
        MethodResult.Skip<Notification.Builder>()
    })

}