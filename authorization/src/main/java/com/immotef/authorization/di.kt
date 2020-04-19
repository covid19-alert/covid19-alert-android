package com.immotef.authorization

import org.koin.dsl.module

/**
 *
 */


val authorizationModule = module {
    single { AuthorizationManager(get(), get()) }
    factory<AuthorizationSaver> { get<AuthorizationManager>() }
    factory<AuthorizationProvider> { get<AuthorizationManager>() }
}