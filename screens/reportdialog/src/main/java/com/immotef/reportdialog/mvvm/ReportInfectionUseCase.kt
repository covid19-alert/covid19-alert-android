package com.immotef.reportdialog.mvvm


import com.immotef.core.common.Mapper
import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.dynamic_links.DeepLinkStateTrigger
import com.immotef.reportdialog.data.InfectionData
import com.immotef.reportdialog.mvvm.network.ReportInfectionApi
import com.immotef.reportdialog.mvvm.network.ReportInfectionRequest

/**
 *
 */


interface ReportInfectionUseCase {
    suspend fun reportInfection(data: InfectionData): DashboardResponse
    suspend fun clearInfectionData()
}


internal class ReportInfectionUseCaseImp(
    private val api: ReportInfectionApi,
    private val deepTrigger: DeepLinkStateTrigger,
    private val mapper: Mapper<InfectionData, ReportInfectionRequest> = InfectionDataMapper()) : ReportInfectionUseCase {

    override suspend fun reportInfection(data: InfectionData): DashboardResponse {
        val response = api.reportInfection(mapper.map(data))
        deepTrigger.clearInfectionState()
        return response
    }

    override suspend fun clearInfectionData() {
        deepTrigger.clearInfectionState()
    }
}


internal class InfectionDataMapper : Mapper<InfectionData, ReportInfectionRequest> {
    override fun map(from: InfectionData): ReportInfectionRequest {
        return ReportInfectionRequest(from.id)
    }
}