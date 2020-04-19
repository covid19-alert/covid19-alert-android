package com.immotef.reportinfection.report.mvvm


import com.immotef.core.common.Mapper
import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.dynamic_links.DeepLinkStateTrigger
import com.immotef.reportinfection.InfectionData
import com.immotef.reportinfection.report.mvvm.network.ReportInfectionApi
import com.immotef.reportinfection.report.mvvm.network.ReportInfectionRequest
import java.util.*

/**
 *
 */


internal interface ReportInfectionUseCase {
    suspend fun reportInfection(data: InfectionData): DashboardResponse
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
}


internal class InfectionDataMapper : Mapper<InfectionData, ReportInfectionRequest> {
    override fun map(from: InfectionData): ReportInfectionRequest {
        val c = Calendar.getInstance()

        c.set(Calendar.YEAR, from.year)
        c.set(Calendar.DAY_OF_MONTH, from.day)
        c.set(Calendar.MONTH, from.month)
        c.set(Calendar.HOUR_OF_DAY, 12)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        return ReportInfectionRequest(Date(c.timeInMillis))
    }
}