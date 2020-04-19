package com.immotef.core

import com.immotef.core.common.StringCreator
import com.immotef.core.common.StringCreatorImp
import com.immotef.core.info.DeviceInfoProvider
import com.immotef.core.info.DeviceInfoProviderImp
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 *
 */


val coreModules = listOf(module {
    single<CoroutineUtils> { CoroutineUtilsImp(get()) }
    factory<DeviceInfoProvider> { DeviceInfoProviderImp() }
    factory<StringCreator> { StringCreatorImp(androidApplication()) }
})