package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.kieronquinn.app.pixelambientmusic.components.settings.SettingsStateHandler
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider
import com.kieronquinn.app.pixelambientmusic.providers.SettingsProvider
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Hooks [SQLiteDatabase] calls to notify relevant providers.
 *  This allows for dynamic updates to the settings state callbacks without needing to query every
 *  few seconds.
 */
class SqliteHooks(private val context: Context): XposedHooks() {

    override val clazz = SQLiteDatabase::class.java

    private fun sendUpdateIfRequired(table: String?){
        if(SettingsStateHandler.SUPERPACKS_SELECTED_PACKS_TABLE_NAME == table) {
            SettingsProvider.notifyUpdate(context)
        }
        if(SettingsStateHandler.SUPERPACKS_PENDING_DOWNLOADS_TABLE_NAME == table){
            LevelDbProvider.notifyUpdate(context)
        }
    }

    private fun insert(
        table: String?,
        nullColumnHack: String?,
        values: ContentValues?
    ) = MethodHook(afterHookedMethod = {
        sendUpdateIfRequired(table)
        MethodResult.Skip<Long>()
    })

    private fun delete(
        table: String?,
        whereClause: String?,
        whereArgs: Array<String?>?
    ) = MethodHook(afterHookedMethod = {
        sendUpdateIfRequired(table)
        MethodResult.Skip<Int>()
    })

    private fun update(
        table: String?,
        values: ContentValues?,
        whereClause: String?,
        whereArgs: Array<String?>?
    ) = MethodHook(afterHookedMethod = {
        sendUpdateIfRequired(table)
        MethodResult.Skip<Int>()
    })

    fun replaceOrThrow(
        table: String?,
        nullColumnHack: String?,
        initialValues: ContentValues?
    ) = MethodHook(afterHookedMethod = {
        sendUpdateIfRequired(table)
        MethodResult.Skip<Long>()
    })

}