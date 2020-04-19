package com.immotef.register

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

/**
 *
 */


internal interface RegisterStateTrigger {
    suspend fun finishRegisterState()
    suspend fun finishRegisterStateWithInfectionState()
    suspend fun openOnboarding()
}


interface RegisterStateListener {
    fun userAlreadyRegistered(): Flow<Unit>
    suspend fun onBoardingOpened(): Flow<Unit>
    suspend fun userRegisterWithInfectedState(): Flow<Unit>
}

@ExperimentalCoroutinesApi
internal class RegisterState : RegisterStateTrigger, RegisterStateListener {

    private val conflatedBroadcastChannel: BroadcastChannel<Unit> = BroadcastChannel(1)
    private val finishRegisterWithInfectedState: BroadcastChannel<Unit> = BroadcastChannel(1)
    private val boardingChannel: BroadcastChannel<Unit> = BroadcastChannel(1)

    override suspend fun finishRegisterState() {
        conflatedBroadcastChannel.send(Unit)
    }

    override suspend fun openOnboarding() {
        boardingChannel.send(Unit)
    }

    override suspend fun finishRegisterStateWithInfectionState() {
        finishRegisterWithInfectedState.send(Unit)
    }

    override suspend fun onBoardingOpened(): Flow<Unit> = boardingChannel.asFlow()

    override fun userAlreadyRegistered(): Flow<Unit> = conflatedBroadcastChannel.asFlow()

    override suspend fun userRegisterWithInfectedState(): Flow<Unit> = finishRegisterWithInfectedState.asFlow()
}