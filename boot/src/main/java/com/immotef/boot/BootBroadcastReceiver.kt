package com.immotef.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.immotef.scanjob.ServiceOpener
import com.immotef.scanjob.ShouldServiceCollect
import org.koin.core.KoinComponent
import org.koin.core.inject

class BootBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val serviceOpener: ServiceOpener by inject()
    private val shouldServiceCollect: ShouldServiceCollect by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (shouldServiceCollect.shouldServiceCollect()) {
            serviceOpener.openService()
        }
    }
}
