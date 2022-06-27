package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.app.blob.BlobStoreManager
import android.app.people.PeopleManager
import android.content.Context
import android.os.Build
import android.view.translation.TranslationManager
import android.view.translation.UiTranslationManager
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

@SuppressLint("NewApi")
class ContextHooks: XposedHooks() {

    companion object {
        private val FAKE_SERVICE_LIST_10 = listOf(
            BlobStoreManager::class.java
        )
        private val FAKE_SERVICE_LIST_11 = listOf(
            PeopleManager::class.java,
            TranslationManager::class.java,
            UiTranslationManager::class.java
        )
    }

    override val clazz = Context::class.java

    private fun getSystemService(clazz: Class<*>) = MethodHook {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            FAKE_SERVICE_LIST_11.find { it.name == clazz.name }?.let {
                return@MethodHook MethodResult.Replace(it.newInstance())
            }
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            FAKE_SERVICE_LIST_10.find { it.name == clazz.name }?.let {
                return@MethodHook MethodResult.Replace(it.newInstance())
            }
        }
        MethodResult.Skip()
    }

}