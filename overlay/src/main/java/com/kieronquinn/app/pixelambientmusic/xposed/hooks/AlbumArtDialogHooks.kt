package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kieronquinn.app.pixelambientmusic.components.albumart.AlbumArtRetriever
import com.kieronquinn.app.pixelambientmusic.xposed.InjectedHooks

/**
 *  Hooks for the Now Playing History song dialog fragment, hooked via the injector
 */
object AlbumArtDialogHooks: InjectedHooks() {

    private const val PREFIX_SONG_ACTIONS_DIALOG_DATA = "SongActionsDialogData"
    private val REGEX_SONG_ACTIONS_DIALOG_DATA =
        "SongActionsDialogData\\{.*firstRecognizedTimestampMillis=(.*), isRemote=.*".toRegex()

    override val label =
        "com/google/intelligence/sense/ambientmusic/history/songactions/SongActionsDialogFragment"

    private val albumArtRetriever
        get() = AlbumArtRetriever.INSTANCE

    private fun any_createView(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        extras: Bundle
    ) = MethodHook(afterHookedMethod = {
        if(!albumArtRetriever.isEnabled()) return@MethodHook MethodResult.Skip()
        val timestamp = thisObject.findTimestamp()?.toLongOrNull()
            ?: return@MethodHook MethodResult.Skip()
        val view = result as View
        val context = view.context
        val albumArtId = context.resources.getIdentifier(
            "song_actions_album_art", "id", context.packageName
        )
        val albumArtImageView = view.findViewById<ImageView>(albumArtId)
        albumArtRetriever.loadTimestampIntoImageView(timestamp, albumArtImageView)
        //Fix insets on < R
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            view.setPadding(0, 0, 0, view.context.getNavigationBarHeight())
        }
        MethodResult.Skip<View>()
    })

    private fun Any.findTimestamp(): String? {
        val songActionsDialogData = clazz.fields.firstNotNullOfOrNull {
            val stringRepresentation = try {
                it.get(this)?.toString()
            }catch (e: Throwable){
                null
            }
            if(stringRepresentation?.startsWith(PREFIX_SONG_ACTIONS_DIALOG_DATA) == true){
                stringRepresentation
            }else null
        } ?: return null
        val matched = REGEX_SONG_ACTIONS_DIALOG_DATA.find(songActionsDialogData)
        return matched?.groupValues?.elementAtOrNull(1)
    }

    private fun Context.getNavigationBarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

}