package com.immotef.beaconscanner.handlers

import android.util.Log
import com.immotef.beacon.BeaconSetupProvider
import com.immotef.beaconscanner.BeaconDataSender
import no.nordicsemi.android.support.v18.scanner.ScanResult

/**
 *
 */


internal class OldIosDevices(private val beaconDataSender: BeaconDataSender,
                             private val beaconSetupProvider: BeaconSetupProvider) : DifferentDeviceHandler {
    override fun handle(scanResult: ScanResult) {
        Log.d("STARY IPONE", scanResult.toString())
    }
}