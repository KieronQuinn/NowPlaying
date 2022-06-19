package com.kieronquinn.app.pixelambientmusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler.SUPERPACKS_DATABASE_NAME
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler.SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler.SUPERPACKS_PENDING_PACKS_TABLE_NAME
import com.kieronquinn.app.pixelambientmusic.providers.SettingsProvider

/**
 *  Debug receiver that clears the pending downloads and packs tables, for use if it is stuck.
 *
 *  Must be broadcast from ADB Shell:
 *
 *  `adb shell am broadcast -a com.kieronquinn.app.pixelambientmusic.CLEAR_PENDING_PACKS -p com.kieronquinn.app.pixelambientmusic`
 */
class ClearPendingReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val superpacksPath = context.getDatabasePath(SUPERPACKS_DATABASE_NAME)
        if(!superpacksPath.exists()) return
        val superpacksDatabase = SQLiteDatabase.openDatabase(
            superpacksPath.absolutePath, null, 0
        )
        superpacksDatabase.execSQL("delete from $SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME")
        superpacksDatabase.execSQL("delete from $SUPERPACKS_PENDING_PACKS_TABLE_NAME")
        superpacksDatabase.close()
        SettingsProvider.notifyUpdate(context)
    }

}