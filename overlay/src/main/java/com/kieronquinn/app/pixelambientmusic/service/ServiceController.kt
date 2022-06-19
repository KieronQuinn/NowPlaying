package com.kieronquinn.app.pixelambientmusic.service

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.kieronquinn.app.ambientmusicmod.IShellProxy
import com.kieronquinn.app.pixelambientmusic.service.ServiceController.Companion.SERVICE_CONNECT_TIMEOUT
import java.util.concurrent.Executors

/**
 *  Manages calls to shell proxy service in Pixel Ambient Music. Automatically connects to
 *  service when required (if the remote service is not connected or the connection is dead),
 *  blocking the current thread in the process. If it can't connect within [SERVICE_CONNECT_TIMEOUT],
 *  an exception is thrown.
 */
class ServiceController(private val context: Context) {

    companion object {
        private const val SERVICE_CONNECT_TIMEOUT = 2500L
        private const val PACKAGE_AMBIENT_MUSIC_MOD = "com.kieronquinn.app.ambientmusicmod"
        private const val ACTION_SHELL_PROXY = "com.kieronquinn.app.ambientmusicmod.SHELL_PROXY"

        @SuppressLint("StaticFieldLeak") //Application context
        private var _INSTANCE: ServiceController? = null

        @JvmStatic
        val INSTANCE
            get() = _INSTANCE ?: throw NullPointerException("INSTANCE is not created")

        fun createInstance(context: Context) {
            _INSTANCE = ServiceController(context)
        }

        internal fun <T> runWithService(block: (IShellProxy) -> T): T {
            return INSTANCE.runWithService(block)
        }
    }

    private val serviceIntent = Intent(ACTION_SHELL_PROXY).apply {
        `package` = PACKAGE_AMBIENT_MUSIC_MOD
    }

    private var shellProxy: IShellProxy? = null
    private var serviceConnection: ServiceConnection? = null
    private val connectionExecutor = Executors.newSingleThreadExecutor()

    private fun <T> runWithService(block: (IShellProxy) -> T): T {
        return block(getProxy())
    }

    @Synchronized
    private fun getProxy(): IShellProxy {
        shellProxy?.let {
            if(!it.safePing()) return@let
            return it
        }
        return connectService()
    }

    private fun connectService(): IShellProxy {
        var service: IShellProxy? = null
        val serviceConnection = object: ServiceConnection {
            override fun onServiceConnected(component: ComponentName, binder: IBinder) {
                val proxy = IShellProxy.Stub.asInterface(binder)
                shellProxy = proxy
                serviceConnection = this
                service = proxy
            }

            override fun onServiceDisconnected(component: ComponentName) {
                shellProxy = null
                serviceConnection = null
            }
        }
        context.bindService(
            serviceIntent,
            Context.BIND_AUTO_CREATE,
            connectionExecutor,
            serviceConnection
        )
        val startTime = System.currentTimeMillis()
        while(service == null){
            if(System.currentTimeMillis() - startTime > SERVICE_CONNECT_TIMEOUT){
                throw RuntimeException("Timeout: Failed to connect to service")
            }
            Thread.sleep(10)
        }
        return service!!
    }

    private fun IShellProxy.safePing(): Boolean {
        return try {
            ping()
        }catch (e: RemoteException){
            false
        }
    }

}