package com.immotef.reportinfection.report.mvvm.network


import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.network.report
import retrofit2.http.Body
import retrofit2.http.POST

/**
 *
 */


internal interface ReportInfectionApi {
    @POST(report)
    suspend fun reportInfection(@Body infection: ReportInfectionRequest): DashboardResponse
}