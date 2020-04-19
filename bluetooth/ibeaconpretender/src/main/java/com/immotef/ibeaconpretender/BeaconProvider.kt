package com.immotef.ibeaconpretender

import com.immotef.beacon.BeaconSetupProvider
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Beacon as AltBeacon


/**
 *
 */

internal data class BeaconStuff(
    val major: Int,
    val minor: Int,
    val serviceUUID: String,
    val privateUUID: String,
    val beacon: AltBeacon,
    val beaconParser: BeaconParser)

internal interface BeaconProvider {
    fun provideBeaconStuff(): BeaconStuff
}

internal class BeaconProviderImp(private val provider: BeaconSetupProvider) : BeaconProvider {
    override fun provideBeaconStuff(): BeaconStuff {
        return runBlocking {
            val beacon = org.altbeacon.beacon.Beacon.Builder()
                .setId1(provider.provideUUID())
                .setId2("FFFF")
                .setId3("FFFF")
                .setDataFields(listOf(1, 2, 3))
                .setManufacturer(0x4C)
                .setTxPower(-59)
                .build()

            val beaconParser = BeaconParser()
                .setBeaconLayout(provider.provideLayout())
            BeaconStuff(
                provider.provideMajor(),
                provider.provideMinor(),
                provider.provideUUID(),
                provider.provideDroppedUUID(),
                beacon,
                beaconParser
            )
        }
    }
}



