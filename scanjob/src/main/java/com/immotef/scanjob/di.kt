package com.immotef.scanjob

import com.immotef.beacon.BEACON_WAY
import com.immotef.btmanager.btManagerModule
import com.immotef.scanjob.manager.ManagerViewModel
import com.immotef.scanjob.service.NotificationFacadeImp
import com.immotef.scanjob.service.SimpleNotificationFacade
import com.immotef.scanjob.service.mvvm.DashboarViewModel
import com.immotef.scanjob.service.mvvm.ScanServiceViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 *
 */
const val INTERACTIVE_NOTIFICATION = true
fun scanJobModules(version: String = BEACON_WAY, interactive: Boolean = INTERACTIVE_NOTIFICATION) = module {
    single {
        if (interactive) {
            NotificationFacadeImp(get(), get())
        } else {
            SimpleNotificationFacade(get(), get())
        }
    }
    single<ServiceOpener> { ServiceOpenerImp(get()) }
    factory { ScanServiceViewModel(get(), get(), get(named(version)), get(named(version)), get(), get()) }
    factory { ManagerViewModel(get(), get(), get()) }
    factory { DashboarViewModel(get(), get(), get()) }
    single { ShouldCollectManager(get(), get(), androidApplication()) }
    factory<ShouldCollectSetter> { get<ShouldCollectManager>() }
    factory<ShouldServiceCollect> { get<ShouldCollectManager>() }
}.plus(btManagerModule)

