package com.immotef.dashboard


import com.immotef.dashboardrepository.data.InfectedState
import com.immotef.dashboardrepository.data.InfectionState
import com.immotef.dashboardrepository.data.RecoveredState

/**
 *
 */


internal suspend fun InfectionState.reportButtonClick(trigger: DashboardStateTrigger) {
    when (this) {
        is InfectedState -> trigger.openRecoveryScreen()
        is RecoveredState -> {
        }
        else -> trigger.openReportInfectionState()
    }
}