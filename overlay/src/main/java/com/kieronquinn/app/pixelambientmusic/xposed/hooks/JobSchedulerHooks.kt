package com.kieronquinn.app.pixelambientmusic.xposed.hooks

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.Context
import com.kieronquinn.app.pixelambientmusic.providers.LevelDbProvider
import com.kieronquinn.app.pixelambientmusic.xposed.XposedHooks

@SuppressLint("PrivateApi")
class JobSchedulerHooks(private val context: Context): XposedHooks() {

    companion object {
        private val DENYLISTED_SERVICES = arrayOf(
            "com.google.android.apps.miphone.aiai.echo.notificationintelligence.scheduler.impl.NotificationJobService",
            "com.google.android.apps.miphone.aiai.echo.scheduler.EchoJobService",
            "com.google.android.apps.miphone.aiai.smartrec.pixelsearch.cronjob.PixelSearchCronJobService"
        )

        private val DOWNLOAD_SERVICE =
            "com.google.android.apps.miphone.aiai.common.superpacks.impl.AiAiPersistentDownloadJobService"

        fun getScheduledDownloadJobs(jobScheduler: JobScheduler): List<Int> {
            return jobScheduler.allPendingJobs.filter {
                it.service.className == DOWNLOAD_SERVICE
            }.map {
                it.id
            }
        }
    }

    override val clazz = Class.forName("android.app.JobSchedulerImpl")

    private fun schedule(jobInfo: JobInfo) = MethodHook(beforeHookedMethod = {
        if(DENYLISTED_SERVICES.contains(jobInfo.service.className)){
            return@MethodHook MethodResult.Replace(JobScheduler.RESULT_SUCCESS)
        }
        MethodResult.Skip()
    }, afterHookedMethod = {
        //Notify provider that a download job has been scheduled so it can be expedited if needed
        if(jobInfo.service.className == DOWNLOAD_SERVICE){
            LevelDbProvider.notifyJobScheduled(context)
        }
        MethodResult.Skip()
    })

}