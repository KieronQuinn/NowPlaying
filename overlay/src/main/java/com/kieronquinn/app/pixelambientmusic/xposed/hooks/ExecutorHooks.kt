package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import java.lang.reflect.Modifier

class ExecutorHooks: XposedHooks() {

    companion object {
        private val EXECUTOR_DENYLIST = arrayOf(
            "normPriorityShared"
        )
    }

    override val clazz = Class.forName("java.util.concurrent.Executors\$DelegatedExecutorService")

    private fun execute(runnable: Runnable) = MethodHook {
        runnable.getAiaiRunnableName()?.let {
            if(EXECUTOR_DENYLIST.contains(it)){
                //return@MethodHook MethodResult.Replace(Unit)
            }
        }
        MethodResult.Skip<Unit>()
    }

    private fun Any.getAiaiRunnableName(): String? {
        //AiAiRunnables have exactly two public final Object fields
        val isAiaiRunnable = this::class.java.declaredFields.count {
            it.type == Object::class.java && Modifier.isFinal(it.modifiers) && Modifier.isPublic(it.modifiers)
        } == 2
        if(!isAiaiRunnable) return null
        //Label is first string object
        return this::class.java.declaredFields.firstNotNullOfOrNull {
            it.isAccessible = true
            it.get(this) as? String
        }
    }

}