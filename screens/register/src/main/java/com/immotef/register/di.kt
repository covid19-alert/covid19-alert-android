package com.immotef.register

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module

/**
 *
 */


@ExperimentalCoroutinesApi
val registerModules = module {
    single<RegisterState>{ RegisterState() }
    factory<RegisterStateTrigger> { get<RegisterState>() }
    factory<RegisterStateListener> { get<RegisterState>() }
}+ registrationFragmentModule