package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

@SuppressLint("PrivateApi")
class JobSchedulerHooks: XposedHooks() {

    companion object {
        private val DENYLISTED_SERVICES = arrayOf(
            "com.google.android.apps.miphone.aiai.echo.notificationintelligence.scheduler.impl.NotificationJobService",
            "com.google.android.apps.miphone.aiai.echo.scheduler.EchoJobService",
            "com.google.android.apps.miphone.aiai.smartrec.pixelsearch.cronjob.PixelSearchCronJobService"
        )
    }

    override val clazz = Class.forName("android.app.JobSchedulerImpl")

    private fun schedule(jobInfo: JobInfo) = MethodHook {
        if(DENYLISTED_SERVICES.contains(jobInfo.service.className)){
            return@MethodHook MethodResult.Replace(JobScheduler.RESULT_SUCCESS)
        }
        MethodResult.Skip()
    }

}