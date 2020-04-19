package com.immotef.scanjob.service

import android.app.Notification
import android.app.NotificationChannel
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

interface NotificationChannelProvider {
    fun provideNotificationChannel(): NotificationChannel
}

const val ANDROID_CHANNEL_ID = "SCAN FOR OTHER PEOPLE"


interface NotificationFacade {

    fun provideNotification(): Notification
    fun handleInfectionState(state: InfectionState)
    fun finish()
}


class NotificationFacadeImp(
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

        val cancelIntent: Intent = ScanService.createIntentStop(context)

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_cancel_24dp,
            context.getString(R.string.stop_scanning),
            PendingIntent.getService(context, 123, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        ).build()

        builder = NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
        notification =
            builder
                .setContentTitle(context.getString(R.string.notification_title))
                .setAutoCancel(true)
                .addAction(action)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_virus)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        return notification
    }

    private val firstLine by lazy { context.getString(R.string.dasboard_first_line_title) }
    private val endOfSecondLine by lazy { context.getString(R.string.dashboard_end_of_second_line) }
    private val thirdLine by lazy { context.getString(R.string.dashboard_third_line) }

    override fun handleInfectionState(state: InfectionState) {

        val text = if (state.shouldShowMultilineTitle) {
            "$firstLine ${context.getString(state.textOfRiskBeingInfected)} $endOfSecondLine $thirdLine"
        } else {
            context.getString(state.textOfRiskBeingInfected)
        }
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
        builder.color = ContextCompat.getColor(context, state.riskTextColor)
        builder.setProgress(100, state.riskLevel, false)
            .setSmallIcon(R.drawable.ic_virus)
        notification = builder.build()
        notificationManager.notify(id, notification)
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


