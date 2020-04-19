package com.immotef.core.extensions

import android.app.job.JobScheduler
import android.content.Context
import android.location.LocationManager
import android.os.Build

/**
 *
 */


fun Context.getJobScheduler(): JobScheduler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getSystemService(
        JobScheduler::class.java
    )
} else {
    getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
}


fun Context.getLocationManager(): LocationManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getSystemService(LocationManager::class.java)
} else {
    getSystemService(Context.LOCATION_SERVICE) as LocationManager
}