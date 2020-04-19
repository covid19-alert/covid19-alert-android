package com.immotef.scanjob

import android.app.job.JobInfo
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.immotef.core.CoroutineUtils
import com.immotef.core.extensions.getJobScheduler
import com.immotef.preferences.PreferencesFacade
import com.immotef.scanjob.service.CheckService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 */


interface ShouldServiceCollect {
    fun shouldServiceCollect(): Boolean
}

internal interface ShouldCollectSetter {
    fun setShouldCollect(should: Boolean)
}

private const val SAVE_COLLECT_KEY = "save_key_12312"
private const val ID_OF_JOB = 28172

internal class ShouldCollectManager(private val preferencesFacade: PreferencesFacade,
                                    private val coroutineUtils: CoroutineUtils,
                                    private val context: Context,
                                    private val timeInterval: Long = 1000 * 60 * 20,
                                    private val openKey: String = SAVE_COLLECT_KEY) : ShouldServiceCollect, ShouldCollectSetter {

    override fun shouldServiceCollect(): Boolean = runBlocking {
        preferencesFacade.retrieveBoolean(openKey)
    }

    override fun setShouldCollect(should: Boolean) {
        coroutineUtils.globalScope.launch {
            preferencesFacade.saveBoolean(should, openKey)
        }
        if (should) {
            scheduleCheckService()
        } else {
            stopServiceCheck()
        }
    }

    private fun scheduleCheckService() {
        val serviceComponent = ComponentName(context, CheckService::class.java)
        val builder = JobInfo.Builder(ID_OF_JOB, serviceComponent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(timeInterval, timeInterval - 5000 * 60)
        } else {
            builder.setPeriodic(timeInterval)
        }

        val jobScheduler = context.getJobScheduler()
        if (jobScheduler.allPendingJobs.none { it.id == ID_OF_JOB }) {
            jobScheduler.schedule(builder.build())
        }
    }

    private fun stopServiceCheck() {
        val jobScheduler = context.getJobScheduler()
        jobScheduler.cancel(ID_OF_JOB)
    }
}