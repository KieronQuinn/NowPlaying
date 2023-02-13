package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.IntentSender
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.google.intelligence.sense.ambientmusic.history.HistoryActivity
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Fixes broken Shortcut Pinning
 */
class ShortcutManagerHooks(
    private val context: Context
): XposedHooks() {

    override val clazz: Class<*> = ShortcutManager::class.java

    @SuppressLint("SoonBlockedPrivateApi")
    private fun requestPinShortcut(
        shortcutInfo: ShortcutInfo, resultIntent: IntentSender
    ) = MethodHook(beforeHookedMethod = {
        args[0] = ShortcutInfo.Builder(context, shortcutInfo.id).apply {
            shortcutInfo.shortLabel?.let { label -> setShortLabel(label) }
            shortcutInfo.intents?.let { intents -> setIntents(intents) }
            shortcutInfo.getIcon()?.let { icon -> setIcon(icon) }
            setActivity(ComponentName(context, HistoryActivity::class.java))
            setRank(0)
            setLongLived(false)
        }.build()
        MethodResult.Skip<Boolean>()
    })

    @SuppressLint("SoonBlockedPrivateApi")
    private fun ShortcutInfo.getIcon(): Icon? {
        return ShortcutInfo::class.java.getDeclaredField("mIcon").apply {
            isAccessible = true
        }.get(this) as? Icon
    }

}