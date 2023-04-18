package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.utils.extensions.getCallingClassName
import com.kieronquinn.app.pixelambientmusic.xposed.InjectedHooks
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import com.kieronquinn.app.pixelambientmusic.xposed.hooks.InjectionHooks.Companion.INJECTED_HOOKS
import java.lang.reflect.Modifier

/**
 *  Hooks the injection method which specifies an unobfuscated class name, allowing us to find
 *  out which class is what.
 *
 *  This class keeps a track of [INJECTED_HOOKS] **objects**, and sets their class names
 *  automatically based on the label sent to the injector.
 */
class InjectionHooks: XposedHooks() {

    private companion object {
        /**
         *  Find the Injection class by using a simple class with an unobfuscated name -
         *  `LevelDbTable`, which has just one `final static` field - the injector.
         */
        private fun findInjectionClass(): Class<*>? {
            val levelDbTable =
                Class.forName("com.google.intelligence.sense.leveldb.LevelDbTable")
            val injector = levelDbTable.declaredFields.firstOrNull {
                Modifier.isStatic(it.modifiers) && Modifier.isFinal(it.modifiers)
            } ?: return null
            return injector.type
        }

        private val INJECTED_HOOKS: Map<String, InjectedHooks> = arrayOf(
            AlbumArtDialogHooks,
            CloudApiHooks,
            SecondaryLanguagesSlicingDelegateHooks,
        ).associateBy {
            it.label
        }
    }

    override val clazz = findInjectionClass()!!

    private fun any_setLabel(className: String) = MethodHook {
        INJECTED_HOOKS[className]?.let {
            val callingClass = getCallingClassName()?.let { name ->
                Thread.currentThread().contextClassLoader!!.loadClass(name)
            } as Class<*>
            it.setClass(callingClass)
        }
        MethodResult.Skip<Any>()
    }

}