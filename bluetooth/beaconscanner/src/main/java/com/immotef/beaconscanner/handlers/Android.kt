package com.immotef.beaconscanner.handlers

import android.os.ParcelUuid
import com.immotef.beacon.Beacon
import com.immotef.beacon.BeaconConverter
import com.immotef.beaconscanner.BeaconDataSender
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

/**
 *
 */
internal class AndroidDifferentDeviceHandler(private val beaconDataSender: BeaconDataSender,
                                             private val uuid: String,
                                             private val beaconConverter: BeaconConverter) : DifferentDeviceHandler {
    override fun handle(scanResult: ScanResult) {

        val bytes = scanResult.scanRecord?.serviceData?.get(ParcelUuid(UUID.fromString(uuid))) ?: return
        beaconConverter.provideMajorMinorFromHex(String(bytes, Charsets.UTF_8))?.let { userIds ->
            Beacon(
                uuid,
                userIds.major,
                userIds.minor,
                Date().time,
                scanResult.rssi,
                scanResult.scanRecord?.txPowerLevel ?: -59
            )
        }?.apply {
            beaconDataSender.sendData(this)
        }
    }
}