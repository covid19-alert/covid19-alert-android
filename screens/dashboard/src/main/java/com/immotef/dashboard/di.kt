package com.immotef.dashboard

import com.immotef.reportrecoverydialog.reportRecoveryModule
import org.koin.dsl.module

/**
 *
 */



internal  val dashboardModule = module{
    single<DashboardState>{ DashboardState() }
    factory<DashboardStateTrigger> {  get<DashboardState>() }
    factory<DashboardStateListener> { get<DashboardState>() }
}
val dashbordsModules = listOf(dashboardViewModule, dashboardModule, reportRecoveryModule)