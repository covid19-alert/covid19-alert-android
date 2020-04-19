package com.immotef.scanjob.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.immotef.dashboardrepository.data.InfectionState
import com.immotef.scanjob.R

/**
 *
 */


class SimpleNotificationFacade(
    private val context: Context,
    private val notificationChannelProvider: NotificationChannelProvider
) : NotificationFacade {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var notification: Notification


    private lateinit var builder: NotificationCompat.Builder
    private var id = ID
    override fun provideNotification(): Notification {
        builder = createBuilder()
        val resultIntent: Intent? = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder = NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
        notification =
            builder
                .setContentTitle(context.getString(R.string.notification_title))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_virus)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        return notification
    }


    override fun handleInfectionState(state: InfectionState) {

    }

    override fun finish() {
        notificationManager.cancel(id)
        notification
    }

    private fun createBuilder(): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannelProvider.provideNotificationChannel()
            NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
        }
    }
}