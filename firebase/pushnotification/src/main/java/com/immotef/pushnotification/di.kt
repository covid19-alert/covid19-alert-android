package com.immotef.pushnotification

import com.immotef.network.getApi
import org.koin.dsl.module

/**
 *
 */


val fcmModule = module {
    single<FirebaseTokenManager> { FirebaseTokenManagerImp(get(),get(),getApi(),get()) }
}