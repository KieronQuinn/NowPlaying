package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.content.ComponentName
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

/**
 *  Android System Intelligence does not have the `INTERNET` permission for transparency reasons,
 *  instead directing internet calls through well-defined routes in `com.google.android.as.oss`.
 *  As the communication between `com.google.android.as` and `com.google.android.as.oss` is
 *  restricted (both ways), we instead use our own implementation of the "Astrea" HTTP gRPC service
 *  from the astrea module. To make ASI use this, we replace the package and class with our own.
 */
class AstreaHooks: XposedHooks() {

    override val clazz = ComponentName::class.java

    private fun constructor_package_name(
        pkg: String,
        name: String
    ) = MethodHook {
        if(pkg == "com.google.android.as.oss" &&
            name == "com.google.android.apps.miphone.astrea.grpc.AstreaGrpcService"){
            args[0] = "com.kieronquinn.app.ambientmusicmod"
            args[1] = "com.google.android.as.oss.grpc.AstreaGrpcService"
        }
        MethodResult.Skip<Void>()
    }

}