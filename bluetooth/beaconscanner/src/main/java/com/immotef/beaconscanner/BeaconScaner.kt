package com.immotef.beaconscanner

import android.bluetooth.BluetoothAdapter
import android.os.ParcelUuid
import android.util.Log

import com.immotef.beacon.Beacon
import com.immotef.beacon.BeaconSetupProvider
import com.immotef.beaconscanner.handlers.DifferentDeviceHandler
import com.immotef.beaconscanner.utils.BeaconMapper
import com.immotef.core.CoroutineUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*

/**
 *
 */


interface BeaconDataProvider {
    fun deviceScanned(): Flow<Beacon>
}

internal interface BeaconDataSender {
    fun sendData(beacon: Beacon)
}

interface BeaconScanner {
    fun scanBeacon()
    fun stopScanning()

}


internal class BeaconDataProviderImp(private val coroutineUtils: CoroutineUtils) : BeaconDataProvider, BeaconDataSender {

    private val deviceScanned: BroadcastChannel<Beacon> = BroadcastChannel(1)
    override fun deviceScanned(): Flow<Beacon> = deviceScanned.asFlow()
    override fun sendData(beacon: Beacon) {
        coroutineUtils.globalScope.launch {
            deviceScanned.send(beacon)
        }
    }
}

@ExperimentalCoroutinesApi
internal class BeaconScannerImp(
    private val mapper: BeaconMapper,
    private val sender: BeaconDataSender,
    private val factory: DifferentDeviceHandler,
    private val beaconSetupProvider: BeaconSetupProvider)
    : BeaconScanner {


    private val nordicScanner = object : no.nordicsemi.android.support.v18.scanner.ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            scanBeacon()
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            mapper.map(result)?.takeIf { mapped -> mapped.uuid == beaconSetupProvider.provideUUID().toLowerCase() }?.apply {
                Log.d("SCANNED", this.toString())
                sender.sendData(this)
                return
            }
            if (conditionThatIsOurApp(result)) {
                factory.handle(result)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { result ->
                if (conditionThatIsOurApp(result)) {
                    factory.handle(result)
                }
            }
        }
    }

    override fun scanBeacon() {
        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled == true) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            val beaconFilter = ScanFilter.Builder().setManufacturerData(0x004c, null).build()
            val iosFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(beaconSetupProvider.provideUUID()))).build()

            try {
                scanner.startScan(
                    listOf(
                        beaconFilter,
                        iosFilter
                    ),
                    ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build(),
                    nordicScanner
                )
            } catch (ex: IllegalArgumentException) {

            }
        }
    }

    override fun stopScanning() {
        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled == true) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(nordicScanner)
        }
    }

    private fun conditionThatIsOurApp(btDevice: ScanResult) =
        btDevice.scanRecord?.serviceUuids?.any { beaconSetupProvider.compareDroppedUUID(it.toString()) } == true ||
                btDevice.scanRecord?.serviceData?.keys?.any { beaconSetupProvider.compareDroppedUUID(it.toString()) } == true

}


@ExperimentalCoroutinesApi
internal class NewBeaconScannerImp(
    private val btAdapter: BluetoothAdapter?,
    private val factory: DifferentDeviceHandler,
    private val beaconSetupProvider: BeaconSetupProvider)
    : BeaconScanner {

    private val parcelUuid = ParcelUuid(UUID.fromString(beaconSetupProvider.provideUUID()))


    private val nordicScanner = object : no.nordicsemi.android.support.v18.scanner.ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (conditionThatIsOurApp(result)) {
                factory.handle(result)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { result ->
                if (conditionThatIsOurApp(result)) {
                    factory.handle(result)
                }
            }
        }
    }

    override fun scanBeacon() {
        if (btAdapter?.isEnabled == true) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.startScan(nordicScanner)
        }
    }


    private fun conditionThatIsOurApp(btDevice: ScanResult) =
        btDevice.scanRecord?.serviceUuids?.any { beaconSetupProvider.compareDroppedUUID(it.toString()) } == true ||
                btDevice.scanRecord?.serviceData?.keys?.any { beaconSetupProvider.compareDroppedUUID(it.toString()) } == true


    override fun stopScanning() {
        val scanner = BluetoothLeScannerCompat.getScanner()
        scanner.stopScan(nordicScanner)
    }


}

