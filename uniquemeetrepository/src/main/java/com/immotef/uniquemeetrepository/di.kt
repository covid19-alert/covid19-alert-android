package com.immotef.uniquemeetrepository

import org.koin.dsl.module

/**
 *
 */


val uniqueMeetModule = module {
    factory<UniqueMeetRepository> { UniqueMeetRepositoryImp(get()) }
}