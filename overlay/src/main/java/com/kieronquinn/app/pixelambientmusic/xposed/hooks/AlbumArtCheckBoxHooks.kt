package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import com.kieronquinn.app.pixelambientmusic.components.albumart.AlbumArtRetriever
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Hooks [CompoundButton.setButtonDrawable], and if the call matches those for the Now Playing
 *  History page (the icon ID and the parent tag), calls out to [AlbumArtRetriever] and replaces
 *  the icon with album art.
 */
class AlbumArtCheckBoxHooks(context: Context): XposedHooks() {

    override val clazz = CompoundButton::class.java

    private val albumArtRetriever
        get() = AlbumArtRetriever.INSTANCE

    private val iconId by lazy {
        context.resources.getIdentifier(
            "multi_select_song_checkbox",
            "drawable",
            context.packageName
        )
    }

    private val onDemandIconId by lazy {
        context.resources.getIdentifier(
            "multi_select_song_checkbox_ondemand",
            "drawable",
            context.packageName
        )
    }

    private val songTimestamp by lazy {
        context.resources.getIdentifier(
            "song_timestamp",
            "id",
            context.packageName
        )
    }

    private fun setButtonDrawable(drawable: Int) = MethodHook {
        val checkBox = thisObject as CheckBox
        if(drawable == onDemandIconId || drawable == iconId && !checkBox.isChecked
            && albumArtRetriever.isEnabled()) {
            checkBox.post {
                val timestamp = checkBox.getTimestampFromTag() ?: return@post
                albumArtRetriever.loadTimestampIntoResult(timestamp, checkBox.measuredWidth){
                    //Check if itemView has since changed
                    if(checkBox.getTimestampFromTag() != timestamp) return@loadTimestampIntoResult
                    checkBox.buttonDrawable = BitmapDrawable(checkBox.context.resources, it)
                }
            }
        }
        MethodResult.Skip<Void>()
    }

    private fun CheckBox.getTimestampFromTag(): Long? {
        if(!isAttachedToWindow) return null
        val parent = parent as? LinearLayout ?: return null
        return parent.getTag(songTimestamp) as? Long
    }

}