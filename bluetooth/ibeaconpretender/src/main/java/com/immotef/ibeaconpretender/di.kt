package com.immotef.ibeaconpretender

import android.bluetooth.BluetoothAdapter
import android.os.Build
import com.immotef.beacon.BEACON_WAY
import com.immotef.beacon.NEW_WAY
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 *
 */


val ibeaconPretenderModule = module {
    single<BeaconTransmitterWrapper>(named(BEACON_WAY)) { BeaconTransmitterWrapperImp(get(), androidApplication()) }
    factory<AdvertiseDataBuilder> {
        if (Build.MANUFACTURER.toLowerCase().contains("samsung")) SamsungAdvertiseDataBuilder(get(), get()) else {
            AndroidAdvertiseDataBuilder(get(), get())
        }
    }
    single<BeaconTransmitterWrapper>(named(NEW_WAY)) { NewServiceUUIDProvider(get(), BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser) }

    single<BeaconProvider> { BeaconProviderImp(get()) }
}