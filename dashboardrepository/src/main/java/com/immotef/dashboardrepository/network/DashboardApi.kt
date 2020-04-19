package com.immotef.dashboardrepository.network

import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.network.dashboardInfected
import retrofit2.http.GET


/**
 *
 */



internal interface DashboardOnlyInfectedApi {
    @GET(dashboardInfected)
    suspend fun getDashboardData(): DashboardResponse
}

interface DashboardProvider {
    suspend fun provideDashboardData(): DashboardResponse
}



internal class DashboardLocallyInfected(private val api: DashboardOnlyInfectedApi) : DashboardProvider {
    override suspend fun provideDashboardData(): DashboardResponse = api.getDashboardData()
}