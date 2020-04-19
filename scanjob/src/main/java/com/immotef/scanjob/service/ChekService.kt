package com.immotef.scanjob.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.immotef.scanjob.ServiceOpener
import org.koin.android.ext.android.inject

class CheckService : JobService() {


    private val serviceOpener: ServiceOpener by inject()


    override fun onStartJob(params: JobParameters?): Boolean {
        serviceOpener.startStopScanningForAndroidReasons()
        Log.d("JOB", "restarting stuff")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}
