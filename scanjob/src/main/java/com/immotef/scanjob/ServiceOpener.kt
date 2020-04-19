package com.immotef.scanjob

import android.content.Context
import com.immotef.scanjob.service.ScanService


/**
 *
 */


interface ServiceOpener {
    fun openService()
    fun stopService()
    fun stopWithoutUploading()
    fun startStopScanningForAndroidReasons()
}

class ServiceOpenerImp(private val context: Context) : ServiceOpener {
    override fun openService() {
        ScanService.start(context)
    }

    override fun stopService() {
        ScanService.stop(context, true)
    }

    override fun stopWithoutUploading() {
        ScanService.stop(context, false)
    }

    override fun startStopScanningForAndroidReasons() {
        ScanService.restartScanningAndTransmitting(context)
    }
}