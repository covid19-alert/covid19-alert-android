package com.immotef.ibeaconpretender

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertiseSettings.ADVERTISE_MODE_LOW_POWER
import android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import com.immotef.beacon.BeaconConverter
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*


/**
 *
 */


interface BeaconTransmitterWrapper {
    suspend fun startTransmitting()
    suspend fun stopTransmitting()
}


internal class BeaconTransmitterWrapperImp(private val beaconProvider: BeaconProvider,
                                           private val context: Context) : BeaconTransmitterWrapper {

    private var beaconTransmitter: BeaconTransmitter? = null

    override suspend fun startTransmitting() {
        if (BluetoothAdapter.getDefaultAdapter()?.bluetoothLeScanner == null) return
        val beaconStuff = beaconProvider.provideBeaconStuff()
        beaconTransmitter?.stopAdvertising()
        beaconTransmitter?.advertiseTxPowerLevel = ADVERTISE_TX_POWER_MEDIUM
        beaconTransmitter?.advertiseMode = ADVERTISE_MODE_LOW_POWER
        beaconTransmitter = BeaconTransmitter(context, beaconStuff.beaconParser)

        beaconTransmitter?.startAdvertising(beaconStuff.beacon, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
            }
        })
    }

    override suspend fun stopTransmitting() {
        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled == true)
            beaconTransmitter?.stopAdvertising()
    }
}


internal class NewServiceUUIDProvider(private val builder: AdvertiseDataBuilder,
                                      private val bleLeAdvertiser: BluetoothLeAdvertiser?) : BeaconTransmitterWrapper {


    private var callback: AdvertiseCallback? = null
    override suspend fun startTransmitting() {

        val settings = AdvertiseSettings.Builder()
            .setConnectable(false)
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .build()


        val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
            }
        }

        callback = advertisingCallback
        bleLeAdvertiser?.startAdvertising(settings, builder.buildData(), callback)
    }

    override suspend fun stopTransmitting() {
        if (BluetoothAdapter.getDefaultAdapter()?.isEnabled == true)
            bleLeAdvertiser?.stopAdvertising(callback)
    }
}


internal interface AdvertiseDataBuilder {
    fun buildData(): AdvertiseData
}

internal class AndroidAdvertiseDataBuilder(
    private val beaconProvider: BeaconProvider,
    private val beaconConverter: BeaconConverter) : AdvertiseDataBuilder {
    override fun buildData(): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .addServiceData(ParcelUuid(UUID.fromString(beaconProvider.provideBeaconStuff().serviceUUID)), beaconConverter.provideMajorMinorHex().toByteArray())
        .build()
}

internal class SamsungAdvertiseDataBuilder(
    private val beaconProvider: BeaconProvider,
    private val beaconConverter: BeaconConverter) : AdvertiseDataBuilder {
    override fun buildData(): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .addManufacturerData(1, beaconConverter.provideMajorMinorHex().toByteArray())
        .addServiceUuid(ParcelUuid(UUID.fromString(beaconProvider.provideBeaconStuff().serviceUUID)))
        .build()
}
