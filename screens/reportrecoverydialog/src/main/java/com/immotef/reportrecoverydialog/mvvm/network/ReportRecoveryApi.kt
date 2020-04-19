package com.immotef.reportrecoverydialog.mvvm.network

import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.network.report_recovery
import retrofit2.http.POST

/**
 *
 */


internal interface ReportRecoveryApi {
    @POST(report_recovery)
    suspend fun postReportRecovery(): DashboardResponse
}