package com.immotef.dynamic_links

import org.koin.dsl.module

/**
 *
 */




val deepLinkModule = module{
    single<DeepLinkState> { DeepLinkState(get(),get()) }
    factory<DeepLinkStateTrigger> { get<DeepLinkState>() }
    factory<DeepLinkInternalTrigger> { get<DeepLinkState>() }
    factory<DeepLinkStateListener> { get<DeepLinkState>() }
    single<DynamicLinkManager> { DynamicLinkManagerImp(get(),get()) }
}