package com.immotef.dynamic_links

import com.immotef.authorization.AuthorizationProvider
import com.immotef.authorization.LoggedState
import com.immotef.core.delegate.BoundCoroutineScopeDelegate
import com.immotef.preferences.PreferencesFacade
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

/**
 *
 */


interface DeepLinkStateTrigger {
    suspend fun setInfectionState(data: ReportInfectionState)
    suspend fun isInfectionStateOn(): Boolean
    suspend fun clearInfectionState()

}

internal interface DeepLinkInternalTrigger : DeepLinkStateTrigger {
    suspend fun triggerInternal()
}

interface DeepLinkStateListener {
    fun reactOnDeepLinkState(): Flow<ReportInfectionState>
}

const val TIME_STAMP_KEY = "timestamp_key"
const val ID_KEY = "id_key"

internal class DeepLinkState(
    private val authorization: AuthorizationProvider,
    private val preferences: PreferencesFacade,
    private val timeStampKey: String = TIME_STAMP_KEY,
    private val idKey: String = ID_KEY
) : DeepLinkStateListener, DeepLinkInternalTrigger {

    private val channel: BroadcastChannel<ReportInfectionState> = BroadcastChannel(1)
    private val scope by BoundCoroutineScopeDelegate()


    override suspend fun setInfectionState(data: ReportInfectionState) {
        scope.launch {
            when (authorization.isLogged()) {
                LoggedState -> {
                    saveReportInfectionState(data)
                    channel.send(data)
                }
                else -> {
                    saveReportInfectionState(data)
                }
            }
        }
    }

    override suspend fun triggerInternal() {
        retrieveReportInfectionState()?.also {
            channel.send(it)
        }
    }

    override suspend fun isInfectionStateOn(): Boolean {
        val state: ReportInfectionState? = retrieveReportInfectionState()
        return state != null
    }

    private suspend fun saveReportInfectionState(data: ReportInfectionState) {
        preferences.saveString(data.id, idKey)
        preferences.saveLong(data.timeStamp, timeStampKey)
    }

    override suspend fun clearInfectionState() {
        saveReportInfectionState(ReportInfectionState("", -1))
    }

    private suspend fun retrieveReportInfectionState(): ReportInfectionState? {
        val id = preferences.retrieveString(idKey) ?: ""
        val timeStamp = preferences.retrieveLong(timeStampKey, -1)
        return if (id == "") {
            null
        } else {
            ReportInfectionState(id, timeStamp)
        }
    }

    override fun reactOnDeepLinkState(): Flow<ReportInfectionState> = channel.asFlow()
}