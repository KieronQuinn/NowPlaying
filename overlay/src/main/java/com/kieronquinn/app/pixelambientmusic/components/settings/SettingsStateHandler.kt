package com.kieronquinn.app.pixelambientmusic.components.settings

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import com.kieronquinn.app.pixelambientmusic.model.BannerMessage
import com.kieronquinn.app.pixelambientmusic.model.SettingsState
import com.kieronquinn.app.pixelambientmusic.model.SettingsStateChange

object SettingsStateHandler {

    private const val SHARED_PREFS_NAME = "now_playing_prefs"
    private const val KEY_ENABLED = "ambient_music_aod_notification_switch"
    private const val KEY_ON_DEMAND_ENABLED = "on_demand_switch"
    private const val VALUE_ON_DEMAND_ENABLED_ENABLED = "askme"
    private const val KEY_ON_DEMAND_SETUP = "aga_fingerprinter_confirmation_key"

    private const val NOTIFICATION_CHANNEL_ID_MUSIC =
        "com.google.intelligence.sense.ambientmusic.MusicNotificationChannel"

    const val SUPERPACKS_DATABASE_NAME = "superpacks.db"
    //Monitored in Sqlite hooks
    const val SUPERPACKS_SELECTED_PACKS_TABLE_NAME = "selected_packs"
    const val SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME = "pending_downloads"
    const val SUPERPACKS_PENDING_PACKS_TABLE_NAME = "pending_packs"
    private const val SUPERPACKS_SUPERPACK_NAME_COLUMN = "superpack_name"
    private const val SUPERPACKS_PARENT_ID_COLUMN = "parent_id"
    private const val SUPERPACK_NAME = "ambientmusic-index-17_09_02"

    private val importantPrefs = arrayOf(
        KEY_ENABLED,
        KEY_ON_DEMAND_ENABLED,
        KEY_ON_DEMAND_SETUP
    )

    fun isImportantPreference(key: String): Boolean {
        return importantPrefs.contains(key)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun Context.areNotificationsEnabled(): Boolean {
        val notificationService =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelEnabled = notificationService
            .getNotificationChannel(NOTIFICATION_CHANNEL_ID_MUSIC)
            ?.importance != NotificationManager.IMPORTANCE_NONE
        val allEnabled = notificationService.areNotificationsEnabled()
        return channelEnabled && allEnabled
    }

    /**
     *  Checks that there are no pending downloads or packs that match [SUPERPACK_NAME], and that
     *  the [SUPERPACK_NAME] pack is selected (ie. finished)
     */
    private fun Context.areSuperpacksDownloaded(): Boolean {
        val database = getDatabasePath(SUPERPACKS_DATABASE_NAME)
        if(!database.exists()) return false
        val sqliteDatabase = SQLiteDatabase.openDatabase(database.absolutePath, null, 0)
        val pendingDownloads = sqliteDatabase.query(
            SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME,
            arrayOf(SUPERPACKS_SUPERPACK_NAME_COLUMN),
            "$SUPERPACKS_SUPERPACK_NAME_COLUMN = ?",
            arrayOf(SUPERPACK_NAME),
            null,
            null,
            null
        )
        val pendingPacks = sqliteDatabase.query(
            SUPERPACKS_PENDING_PACKS_TABLE_NAME,
            arrayOf(SUPERPACKS_PARENT_ID_COLUMN),
            "$SUPERPACKS_PARENT_ID_COLUMN = ?",
            arrayOf(SUPERPACK_NAME),
            null,
            null,
            null
        )
        val selectedPacks = sqliteDatabase.query(
            SUPERPACKS_SELECTED_PACKS_TABLE_NAME,
            arrayOf(SUPERPACKS_SUPERPACK_NAME_COLUMN),
            "$SUPERPACKS_SUPERPACK_NAME_COLUMN = ?",
            arrayOf(SUPERPACK_NAME),
            null,
            null,
            null
        )
        return (selectedPacks.count != 0 && pendingDownloads.count == 0 && pendingPacks.count == 0).also {
            selectedPacks.close()
            pendingDownloads.close()
            pendingPacks.close()
            sqliteDatabase.close()
        }
    }

    fun getSuperpackDownloadCount(context: Context): Int {
        val database = context.getDatabasePath(SUPERPACKS_DATABASE_NAME)
        if(!database.exists()) return 0
        val sqliteDatabase = SQLiteDatabase.openDatabase(database.absolutePath, null, 0)
        val pendingDownloads = sqliteDatabase.query(
            SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME,
            arrayOf(SUPERPACKS_SUPERPACK_NAME_COLUMN),
            "$SUPERPACKS_SUPERPACK_NAME_COLUMN = ?",
            arrayOf(SUPERPACK_NAME),
            null,
            null,
            null
        )
        return pendingDownloads.count.also {
            pendingDownloads.close()
            sqliteDatabase.close()
        }
    }

    private fun SharedPreferences.isEnabled(): Boolean {
        return getBoolean(KEY_ENABLED, false)
    }

    private fun SharedPreferences.isOnDemandEnabled(): Boolean {
        return getString(KEY_ON_DEMAND_ENABLED, "") == VALUE_ON_DEMAND_ENABLED_ENABLED
    }

    private fun SharedPreferences.isSearchButtonBeingSetUp(): Boolean {
        return isOnDemandEnabled() && !getBoolean(KEY_ON_DEMAND_SETUP, false)
    }

    private fun Context.getBannerMessage(sharedPreferences: SharedPreferences): BannerMessage? {
        if(!sharedPreferences.isEnabled()) return null //No banner if disabled
        if(!areSuperpacksDownloaded()) return BannerMessage.DOWNLOADING
        /*if(sharedPreferences.isSearchButtonBeingSetUp()) Not reliable on Pixels and we have better detection methods anyway
            return BannerMessage.SEARCH_BUTTON_BEING_SET_UP*/
        return null
    }

    fun generateSettingsState(context: Context): SettingsState {
        val sharedPrefs = getSharedPreferences(context)
        val enabled = sharedPrefs.isEnabled()
        val onDemand = sharedPrefs.isOnDemandEnabled()
        val notificationsEnabled = context.areNotificationsEnabled()
        val bannerMessage = context.getBannerMessage(sharedPrefs)
        return SettingsState(enabled, onDemand, notificationsEnabled, bannerMessage, null)
    }

    fun saveSettingsStateChange(context: Context, settingsStateChange: SettingsStateChange) {
        val sharedPrefs = getSharedPreferences(context)
        settingsStateChange.mainEnabled?.let {
            sharedPrefs.edit().putBoolean(KEY_ENABLED, it).commit()
        }
        settingsStateChange.onDemandEnabled?.let {
            val value = if(it) VALUE_ON_DEMAND_ENABLED_ENABLED else ""
            sharedPrefs.edit().putString(KEY_ON_DEMAND_ENABLED, value).commit()
        }
    }

    fun registerListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(listener)
    }

}