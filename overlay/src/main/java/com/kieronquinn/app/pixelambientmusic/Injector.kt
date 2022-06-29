package com.kieronquinn.app.pixelambientmusic

import android.content.Context
import com.google.android.apps.miphone.aiai.AiaiApplication
import com.kieronquinn.app.pixelambientmusic.components.albumart.AlbumArtRetriever
import com.kieronquinn.app.pixelambientmusic.config.DeviceConfigOverrides
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider
import com.kieronquinn.app.pixelambientmusic.service.ServiceController
import com.kieronquinn.app.pixelambientmusic.utils.extensions.clearDumpFiles
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.io.File
import java.security.Security

/**
 *  Main entry point for modification, sitting above [AiaiApplication].
 */
class Injector: AiaiApplication() {

    companion object {
        const val DEBUG = false
    }

    override fun attachBaseContext(base: Context) {
        HiddenApiBypass.addHiddenApiExemptions("")
        extractAssets(base)
        fixBouncyCastle()
        DeviceConfigOverrides.populateValues(base)
        ServiceController.createInstance(base)
        AlbumArtRetriever.createInstance(base)
        XposedHooks.setupHooks(base)
        if(!DEBUG){
            //Clean up any previously left over dump files
            base.clearDumpFiles()
        }
        super.attachBaseContext(base)
    }

    /**
     *  Extract the leveldb database (which matters) and the dummy descriptor and sound model
     *  (which do not). The database is used as the core shard, whereas the descriptor and
     *  sound model only need to satisfy *existing* so are empty files.
     */
    private fun extractAssets(context: Context) {
        val outDir = File(context.filesDir, LevelDbProvider.PAM_FOLDER).apply {
            mkdirs()
        }
        val assetFiles = arrayOf(
            "matcher_tah.leveldb",
            "music_detector.descriptor",
            "music_detector.sound_model"
        ).filterNot {
            File(outDir, it).exists()
        }
        assetFiles.forEach {
            context.assets.open(it).use { input ->
                File(outDir, it).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    /**
     *  On some devices the MessageDigest calls in ASI will fail, so use our own BouncyCastle to
     *  fix this.
     */
    private fun fixBouncyCastle() {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
    }

}