package com.immotef.dashboardrepository

import com.immotef.dashboardrepository.network.DashboardLocallyInfected
import com.immotef.dashboardrepository.network.DashboardProvider
import com.immotef.network.getApi
import org.koin.dsl.module

/**
 *
 */


fun dashboardRepositoryModule(traceLocally: Boolean = false) = module {
    single<DashboardRepository> { DashboardRepositoryImp(get(), get(), get(), RiskDataFactoryImp(), get()) }
    factory<LastDashboardDataPersist> { LastDashboardDataPersistImp(get()) }
    factory<DashboardDataFactory> { DashboardDataFactoryImp(OnlyInfectedIdsDashboardDataFactory(get())) }
    single<DashboardProvider> {
        DashboardLocallyInfected(getApi())

    }
}
