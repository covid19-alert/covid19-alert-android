package com.immotef.reportrecoverydialog.mvvm

import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.reportrecoverydialog.mvvm.network.ReportRecoveryApi

/**
 *
 */


interface ReportRecoveryUseCase {
    suspend fun reportRecovery(): DashboardResponse
}


internal class ReportRecoveryUseCaseImp(private val reportRecoveryApi: ReportRecoveryApi) : ReportRecoveryUseCase {
    override suspend fun reportRecovery() = reportRecoveryApi.postReportRecovery()
}