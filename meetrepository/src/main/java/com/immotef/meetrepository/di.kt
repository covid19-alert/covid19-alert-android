package com.immotef.meetrepository


import org.koin.dsl.module

/**
 *
 */


val meetRepositoryModule = module {
    single<MeetRepository> { MeetRepositoryImp(get(), beaconCloseChecker = object : BeaconCloseChecker {}) }

}