package com.immotef.dashboard

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

/**
 *
 */

internal interface DashboardStateTrigger {
    suspend fun openInfectedPeopleList()
    suspend fun openUrl(url: String)
    suspend fun openReportInfectionState()
    suspend fun openRecoveryScreen()
}


interface DashboardStateListener {
    fun openInfectedPeople(): Flow<Unit>
    fun openReportInfection(): Flow<Unit>
    fun openRecoveryFlow(): Flow<Unit>
    fun openUrl(): Flow<String>
}

@ExperimentalCoroutinesApi
internal class DashboardState : DashboardStateTrigger, DashboardStateListener {

    private val broadcastChannel: BroadcastChannel<Unit> = BroadcastChannel(1)
    private val broadcastStringChannel: BroadcastChannel<String> = BroadcastChannel(1)
    private val broadcastReportChannel: BroadcastChannel<Unit> = BroadcastChannel(1)
    private val broadcastRecoveryChannel: BroadcastChannel<Unit> = BroadcastChannel(1)

    override suspend fun openInfectedPeopleList() = broadcastChannel.send(Unit)
    override suspend fun openUrl(url: String) = broadcastStringChannel.send(url)
    override suspend fun openReportInfectionState() = broadcastReportChannel.send(Unit)
    override suspend fun openRecoveryScreen() = broadcastRecoveryChannel.send(Unit)

    override fun openInfectedPeople(): Flow<Unit> = broadcastChannel.asFlow()
    override fun openReportInfection(): Flow<Unit> = broadcastReportChannel.asFlow()
    override fun openRecoveryFlow(): Flow<Unit> = broadcastRecoveryChannel.asFlow()
    override fun openUrl(): Flow<String> = broadcastStringChannel.asFlow()
}