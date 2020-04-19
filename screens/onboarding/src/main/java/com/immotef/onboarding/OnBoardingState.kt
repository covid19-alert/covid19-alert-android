package com.immotef.onboarding

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


/**
 *
 */


internal interface OnBoardingStateTrigger {
    suspend fun finishOnBoarding()
}

interface OnBoardingStateListener {
    fun onBoardingFinished(): Flow<Unit>
}

@ExperimentalCoroutinesApi
internal class OnBoardingState : OnBoardingStateTrigger, OnBoardingStateListener {

    private val broadcastChannel: BroadcastChannel<Unit> = BroadcastChannel(1)

    override suspend fun finishOnBoarding() = broadcastChannel.send(Unit)

    override fun onBoardingFinished(): Flow<Unit> = broadcastChannel.asFlow()
}