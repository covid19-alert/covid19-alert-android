package com.immotef.beaconscanner.handlers



import android.os.ParcelUuid
import com.immotef.beacon.BeaconConverter
import com.immotef.beacon.BeaconSetupProvider
import com.immotef.beaconscanner.BeaconDataSender
import no.nordicsemi.android.support.v18.scanner.ScanResult

import java.util.*


/**
 *
 */


interface DifferentDeviceHandler {
    fun handle(scanResult: ScanResult)
}

internal class DeviceHandlerFactoryImp(private val beaconSetupProvider: BeaconSetupProvider,
                                       private val beaconConverter: BeaconConverter,
                                       private val beaconDataSender: BeaconDataSender) : DifferentDeviceHandler {

    override fun handle(scanResult: ScanResult) = when {
        androidCondition(scanResult) -> AndroidDifferentDeviceHandler(beaconDataSender, beaconSetupProvider.provideUUID(), beaconConverter)
        samsungCondition(scanResult) -> SamsungDifferentDeviceHandler(beaconDataSender, beaconSetupProvider.provideUUID(), beaconConverter)
        iphoneWithBt5Condition(scanResult) -> NewIOSDifferentDeviceHandler(beaconDataSender, beaconSetupProvider)
        else -> OldIosDevices(beaconDataSender, beaconSetupProvider)
    }.handle(scanResult)


    private fun iphoneWithBt5Condition(it: ScanResult): Boolean {
        return it.scanRecord?.serviceUuids?.any {
            beaconSetupProvider.compareDroppedUUID(
                it.toString(),
                shouldCheckAreNotExactlyTheSame = true
            )
        } == true
    }

    private fun androidCondition(it: ScanResult) = it.scanRecord?.serviceData?.get(ParcelUuid(UUID.fromString(beaconSetupProvider.provideUUID()))) != null

    private fun samsungCondition(it: ScanResult) = it.scanRecord?.serviceData?.any { it.value != null } == true


}