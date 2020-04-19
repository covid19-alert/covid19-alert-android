package com.immotef.beaconscanner.handlers

import com.immotef.beacon.Beacon
import com.immotef.beacon.BeaconSetupProvider
import com.immotef.beaconscanner.BeaconDataSender
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

/**
 *
 */
internal class NewIOSDifferentDeviceHandler(private val beaconDataSender: BeaconDataSender,
                                            private val beaconSetupProvider: BeaconSetupProvider) : DifferentDeviceHandler {
    override fun handle(scanResult: ScanResult) {
        scanResult.scanRecord?.serviceUuids?.find { uuid ->
            beaconSetupProvider.compareDroppedUUID(
                uuid.toString(),
                shouldCheckAreNotExactlyTheSame = true
            )
        }
            ?.let { parcelUUID ->
                beaconSetupProvider.takeMajorMinorFromUUID(parcelUUID.uuid.toString())
            }?.let { majMin ->
                Beacon(
                    beaconSetupProvider.provideUUID(),
                    majMin.major,
                    majMin.minor,
                    Date().time,
                    scanResult.rssi,
                    scanResult.scanRecord?.txPowerLevel ?: -59
                ).apply {
                    beaconDataSender.sendData(this)
                }
            }
    }
}
