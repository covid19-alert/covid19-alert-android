package com.immotef.scanjob.service.mvvm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.immotef.beaconscanner.BeaconDataProvider
import com.immotef.beaconscanner.BeaconScanner
import com.immotef.btmanager.BtManager
import com.immotef.core.CoroutineUtils
import com.immotef.core.errors.ErrorWrapper
import com.immotef.ibeaconpretender.BeaconTransmitterWrapper
import com.immotef.meetrepository.MeetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 *
 */


internal class ScanServiceViewModel(
    private val coroutineUtils: CoroutineUtils,
    private val meetRepository: MeetRepository,
    private val scanner: BeaconScanner,
    private val transmitter: BeaconTransmitterWrapper,
    private val btManager: BtManager,
    private val beaconDataProvider: BeaconDataProvider
) {

    private val scope = CoroutineScope(coroutineUtils.main + SupervisorJob())

    private val errorProcessor: MutableLiveData<ErrorWrapper> = MutableLiveData()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        errorProcessor.postValue(coroutineUtils.produce(throwable))
    }

    init {
        scope.launch(errorHandler) {
            btManager.btOnStream().collect {
                if (it) {
                    scanner.scanBeacon()
                    transmitter.startTransmitting()
                } else {
                    scanner.stopScanning()
                    transmitter.stopTransmitting()
                }
            }
        }
    }

    private var job: Job? = null

    fun create() {
        btManager.registerReceiver()
        if (btManager.turnBtOn()) {
            scope.launch {
                transmitter.startTransmitting()
                scanner.scanBeacon()
            }
        }
    }

    fun start(): Boolean {
        btManager.turnBtOn()
        if (job == null) {
            job = scope.launch(errorHandler) {
                beaconDataProvider.deviceScanned().collect {
                    meetRepository.addBeaconEvent(it)
                }
            }
            return true
        }
        return false

    }


    fun onDestroy() {
        btManager.unregisterReceiver()
        scope.launch {
            transmitter.stopTransmitting()
        }
        scope.cancel()
        scanner.stopScanning()
    }

    fun restart() {
        scope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                transmitter.stopTransmitting()
                scanner.stopScanning()
                Log.d("RESTARTING", "scanning and transmitting stopped")
                delay(6000)
                scanner.scanBeacon()
                transmitter.startTransmitting()
                Log.d("RESTARTING", "scanning and transmitting started")
            }
        }
    }
}