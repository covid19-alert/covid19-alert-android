package com.immotef.beacon

import org.koin.dsl.module

/**
 *
 */


fun beaconModule(uuid: String) = module {
    single<BeaconManager> { BeaconManagerImp(get(), UUID = uuid) }
    factory<BeaconSetupProvider> { get<BeaconManager>() }
    factory<BeaconSetup> { get<BeaconManager>() }
    factory<BeaconConverter> { get<BeaconManager>() }
}