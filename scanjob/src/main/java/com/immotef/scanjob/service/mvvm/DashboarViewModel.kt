package com.immotef.scanjob.service.mvvm

import com.immotef.core.CoroutineUtils
import com.immotef.dashboardrepository.DashboardRepository
import com.immotef.scanjob.service.NotificationFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 *
 */


internal class DashboarViewModel(
    private val coroutineUtils: CoroutineUtils,
    private val repository: DashboardRepository,
    private val notificationFacade: NotificationFacade
) {

    private val scope = CoroutineScope(coroutineUtils.main + SupervisorJob())
    private var job: Job? = null
    fun start() {
        job = scope.launch {
            repository.provideInfectedState().distinctUntilChanged(areEquivalent = { old, new ->
                old.riskLevel == new.riskLevel && old.textOfRiskBeingInfected == new.textOfRiskBeingInfected
            }).collect {
                notificationFacade.handleInfectionState(it)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
