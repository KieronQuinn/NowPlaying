package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.app.people.PeopleManager
import android.content.Context
import android.os.Build
import android.view.translation.TranslationManager
import android.view.translation.UiTranslationManager
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

@SuppressLint("NewApi")
class ContextHooks: XposedHooks() {

    companion object {
        private val FAKE_SERVICE_LIST = listOf(
            PeopleManager::class.java,
            TranslationManager::class.java,
            UiTranslationManager::class.java
        )
    }

    override val clazz = Context::class.java

    private fun getSystemService(clazz: Class<*>) = MethodHook {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return@MethodHook MethodResult.Skip<Any>()
        }
        FAKE_SERVICE_LIST.find { it.name == clazz.name }?.let {
            return@MethodHook MethodResult.Replace(it.newInstance())
        }
        MethodResult.Skip()
    }

}