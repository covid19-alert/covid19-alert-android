package com.immotef.infectedrepository

import org.koin.dsl.module

/**
 *
 */


val infectedRepositoryModule = module {
    single<InfectedRepository> { InfectedRepositoryImp(get(), get()) }
}