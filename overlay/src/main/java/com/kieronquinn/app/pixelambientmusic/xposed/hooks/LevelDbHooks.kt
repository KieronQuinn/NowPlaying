package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import com.google.android.gms.leveldb.LevelDbTable
import com.kieronquinn.app.pixelambientmusic.utils.extensions.isArmv7
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Redirects LevelDb calls to gms-extracted LevelDB JNI library that is compatible
 */
class LevelDbHooks: XposedHooks() {

    override val clazz: Class<*> =
        Class.forName("com.google.intelligence.sense.leveldb.LevelDbTable")

    fun nativeLoad(file: String) = MethodHook(beforeHookedMethod = {
        if(isArmv7){
            MethodResult.Replace(LevelDbTable.nativeOpen(file))
        }else{
            MethodResult.Skip()
        }
    })

    fun nativeGet(pointer: Long, byteArray: ByteArray) = MethodHook(beforeHookedMethod = {
        if(isArmv7){
            MethodResult.Replace(LevelDbTable.nativeGet(pointer, byteArray))
        }else{
            MethodResult.Skip()
        }
    })

    fun nativeClose(pointer: Long) = MethodHook(beforeHookedMethod = {
        if(isArmv7){
            MethodResult.Replace(LevelDbTable.nativeClose(pointer))
        }else{
            MethodResult.Skip()
        }
    })

}