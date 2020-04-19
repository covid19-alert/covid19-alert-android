package com.immotef.scanjob.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.immotef.core.CoroutineUtils
import com.immotef.scanjob.ShouldCollectSetter
import com.immotef.scanjob.manager.ManagerViewModel
import com.immotef.scanjob.service.mvvm.DashboarViewModel
import com.immotef.scanjob.service.mvvm.ScanServiceViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject


const val ID = 123

private const val STOP = "bolean_stop_collecting"
private const val RESTART = "boolean_restart_service"

class ScanService : Service() {
    companion object {

        fun start(context: Context) {
            val intent = createIntent(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context, uplaod: Boolean = true) {
            val intent = createIntentStop(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun createIntentStop(context: Context): Intent {
            return createIntent(context).also {
                it.putExtra(STOP, true)
            }
        }


        private fun createIntent(context: Context): Intent = Intent(context, ScanService::class.java)

        fun restartScanningAndTransmitting(context: Context) {
            val intent = createIntent(context).also {
                it.putExtra(RESTART, true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

    }

    private val viewModel: ScanServiceViewModel by inject()

    private val managerViewModel: ManagerViewModel by inject()

    private val notificationFacade: NotificationFacade by inject()

    private val shouldCollectSetter: ShouldCollectSetter by inject()

    private val dashboardViewModel: DashboarViewModel by inject()

    private val coroutineUtils: CoroutineUtils by inject()
    override fun onCreate() {
        super.onCreate()
        viewModel.create()
    }


    @InternalCoroutinesApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        if (intent?.getBooleanExtra(RESTART, false) == true) {

            viewModel.restart()

        } else if (viewModel.start()) {
            startForeground(ID, notificationFacade.provideNotification())
            shouldCollectSetter.setShouldCollect(true)
            dashboardViewModel.start()
        }

        if (intent?.getBooleanExtra(STOP, false) == true) {
            dashboardViewModel.stop()
            managerViewModel.stop()
            shouldCollectSetter.setShouldCollect(false)
            stopSelf()

        } else {
            managerViewModel.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        viewModel.onDestroy()
        managerViewModel.onDestroy()
        notificationFacade.finish()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
