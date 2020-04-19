package com.immotef.coronavirusblockade

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.immotef.scanjob.R
import com.immotef.scanjob.service.ANDROID_CHANNEL_ID
import com.immotef.scanjob.service.NotificationChannelProvider

/**
 *
 */

const val ANDROID_CHANNEL_ID = "SCAN FOR OTHER PEOPLE"

class NotificationChannelProviderImp(private val context: Context) : NotificationChannelProvider {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun provideNotificationChannel(): NotificationChannel {
        val importance = NotificationManager.IMPORTANCE_HIGH
        return notificationManager.getNotificationChannel(ANDROID_CHANNEL_ID) ?: NotificationChannel(
            ANDROID_CHANNEL_ID,
            context.resources.getString(R.string.notification_channel),
            importance
        ).also {
            it.enableLights(true)
            it.lightColor = Color.RED
            it.setSound(null, null)
            notificationManager.createNotificationChannel(it)
        }
    }
}