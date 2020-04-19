package com.immotef.dashboardrepository

import com.immotef.dashboardrepository.data.DashboardData
import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.infectedrepository.InfectedRepository
import com.immotef.uniquemeetrepository.UniqueMeet

/**
 *
 */


internal interface DashboardDataFactory {
    suspend fun produceDashboardData(dashboardResponse: DashboardResponse, uniqueIds: List<UniqueMeet>): DashboardData
}

internal class DashboardDataFactoryImp(
    private val infectedIdsFactory: DashboardDataFactory
) : DashboardDataFactory {
    override suspend fun produceDashboardData(dashboardResponse: DashboardResponse, uniqueIds: List<UniqueMeet>): DashboardData =
        (infectedIdsFactory).produceDashboardData(dashboardResponse, uniqueIds)
}


internal class OnlyInfectedIdsDashboardDataFactory(private val infectedRepository: InfectedRepository) : DashboardDataFactory {
    override suspend fun produceDashboardData(dashboardResponse: DashboardResponse, uniqueIds: List<UniqueMeet>): DashboardData {
        val infectedPeopleSize = uniqueIds.filter { unique -> dashboardResponse.infectedList?.any { it == unique.id } ?: false }.size
        infectedRepository.saveInfected(dashboardResponse.infectedList)
        return DashboardData(
            uniqueIds.size,
            infectedPeopleSize,
            when {
                dashboardResponse.isInfected -> 100
                dashboardResponse.isRecovered -> 0
                infectedPeopleSize < 1 -> 25
                infectedPeopleSize < 3 -> 50
                else -> 75
            },
            dashboardResponse.isInfected,
            dashboardResponse.isRecovered
        )
    }
}