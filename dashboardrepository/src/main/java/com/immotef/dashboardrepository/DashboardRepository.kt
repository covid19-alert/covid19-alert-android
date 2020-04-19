package com.immotef.dashboardrepository

import com.immotef.dashboardrepository.data.DashboardResponse
import com.immotef.dashboardrepository.data.InfectionState
import com.immotef.dashboardrepository.network.DashboardProvider
import com.immotef.uniquemeetrepository.UniqueMeetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*


/**
 *
 */


interface DashboardRepository {
    suspend fun reloadDashBoardData()
    fun provideInfectedState(): Flow<InfectionState>
    suspend fun updateWithDashboardResponse(dashboardResponse: DashboardResponse)
}


@ExperimentalCoroutinesApi
internal class DashboardRepositoryImp(
    private val dashboardApi: DashboardProvider,
    private val repository: UniqueMeetRepository,
    private val lastDashboardDataPersist: LastDashboardDataPersist,
    private val factory: RiskDataFactory,
    private val dashboardDataFactory: DashboardDataFactory
) : DashboardRepository {

    private val channel: BroadcastChannel<DashboardResponse>

    init {
        val lastSaved = lastDashboardDataPersist.provideLastDashboardData()
        factory.setFirstInfectionState(lastSaved)
        channel = ConflatedBroadcastChannel(DashboardResponse(emptyList(), lastSaved.risk, 0, lastSaved.isInfected, lastSaved.isRecovered, null))
    }

    override suspend fun reloadDashBoardData() {
        val data = dashboardApi.provideDashboardData()
        channel.send(data)
    }

    override fun provideInfectedState(): Flow<InfectionState> = repository.provideUniqueMeets()
        .combine(channel.asFlow()) { listUnique, dashboardResponse ->
            dashboardDataFactory.produceDashboardData(dashboardResponse, listUnique)
        }
        .onEach { p -> lastDashboardDataPersist.persistLastDashboardData(p) }
        .map { a -> factory.produce(a) }
        .distinctUntilChanged()


    override suspend fun updateWithDashboardResponse(dashboardResponse: DashboardResponse) = channel.send(dashboardResponse)
}
