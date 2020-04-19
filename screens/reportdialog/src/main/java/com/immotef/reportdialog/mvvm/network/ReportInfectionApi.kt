package com.immotef.reportdialog.mvvm.network

import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.network.report_deepling
import retrofit2.http.Body
import retrofit2.http.POST

/**
 *
 */


internal interface ReportInfectionApi {
    @POST(report_deepling)
    suspend fun reportInfection(@Body infection: ReportInfectionRequest): DashboardResponse
}