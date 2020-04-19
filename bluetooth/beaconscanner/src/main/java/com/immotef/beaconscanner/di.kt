package com.immotef.beaconscanner

import android.bluetooth.BluetoothAdapter
import com.immotef.beacon.BEACON_WAY
import com.immotef.beacon.NEW_WAY
import com.immotef.beaconscanner.handlers.DeviceHandlerFactoryImp
import com.immotef.beaconscanner.handlers.DifferentDeviceHandler
import com.immotef.beaconscanner.utils.BeaconMapperImp
import org.koin.core.qualifier.named
import org.koin.dsl.module


/**
 *
 */


val btScannerModule = module {
    single<BeaconScanner>(named(NEW_WAY)) { NewBeaconScannerImp(BluetoothAdapter.getDefaultAdapter(), get(), get()) }
    single { BeaconDataProviderImp(get()) }
    single<BeaconDataProvider> { get<BeaconDataProviderImp>() }
    single<BeaconDataSender> { get<BeaconDataProviderImp>() }
    factory<DifferentDeviceHandler> { DeviceHandlerFactoryImp(get(), get(), get()) }
    single<BeaconScanner>(named(BEACON_WAY)) { BeaconScannerImp(BeaconMapperImp(), get(), get(), get()) }

}