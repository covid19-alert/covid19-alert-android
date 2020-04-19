package com.immotef.beaconscanner.utils


import com.immotef.beacon.Beacon
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

/**
 *
 */


internal interface BeaconMapper {
    fun map(rxBeacon: ScanResult): Beacon?

}

internal class BeaconMapperImp : BeaconMapper {
    override fun map(rxBeacon: ScanResult): Beacon? = rxBeacon.scanRecord?.bytes?.let { scanData ->
        if (scanData[4] == 0x02.toByte() && scanData[5] == 0x15.toByte()) { // iBeacon indicator
            return@let getBeaconFromByteArray(scanData, 4, rxBeacon.rssi)
        } else if (scanData[7] == 0x02.toByte() && scanData[8] == 0x15.toByte()) {
            return@let getBeaconFromByteArray(scanData, 7, rxBeacon.rssi)

        } else
            null
    }


    private fun getBeaconFromByteArray(scanData: ByteArray, first: Int, rssi: Int): Beacon {
        val uuid: UUID = getGuidFromByteArray(scanData.copyOfRange(first + 2, first + 18))
        val major: Int = (scanData[first + 18] and 0xff.toByte()) * 0x100 + (scanData[first + 19] and 0xff.toByte())
        val minor: Int = (scanData[first + 20] and 0xff.toByte()) * 0x100 + (scanData[first + 21] and 0xff.toByte())
        val txPower: Int = scanData[first + 22].toByte().toInt()
        return Beacon(
            uuid.toString(),
            major,
            minor,
            Date().time,
            rssi = rssi,
            txPower = txPower
        )
    }


    private fun getGuidFromByteArray(bytes: ByteArray): UUID {
        val bb = ByteBuffer.wrap(bytes)
        return UUID(bb.long, bb.long)
    }
}