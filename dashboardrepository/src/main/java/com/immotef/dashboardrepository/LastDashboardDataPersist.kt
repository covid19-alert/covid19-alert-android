package com.immotef.dashboardrepository

import com.immotef.dashboardrepository.data.DashboardData
import com.immotef.preferences.PreferencesFacade
import kotlinx.coroutines.runBlocking


/**
 *
 */


internal interface LastDashboardDataPersist {
    suspend fun persistLastDashboardData(data: DashboardData)
    fun provideLastDashboardData(): DashboardData
}

internal class LastDashboardDataPersistImp(
    private val persistFacade: PreferencesFacade,
    private val saveKey: String = "save_key_1292"
) : LastDashboardDataPersist {
    override suspend fun persistLastDashboardData(data: DashboardData) {
        persistFacade.putObject(saveKey, data)
    }

    override fun provideLastDashboardData(): DashboardData {
        return runBlocking { persistFacade.getObject(saveKey, DashboardData::class.java) ?: DashboardData(0, 0, 0, isInfected = false, isRecovered = false) }
    }
}