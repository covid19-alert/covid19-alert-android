package com.immotef.featureflag

import com.immotef.featureflag.load.SaveFeatureFlagUseCase
import com.immotef.featureflag.load.SaveFeatureFlagUseCaseImp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 *
 */


val featureFlagModule = module {
    single<FeatureFlagManager> { FeatureFlagManagerImp(get(), get(), get()) }
    factory<SaveFeatureFlagUseCase> { SaveFeatureFlagUseCaseImp(androidContext(), get(), get()) }
}