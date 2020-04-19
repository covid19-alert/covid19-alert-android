package com.immotef.dashboard.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.immotef.core.CoroutineUtils
import com.immotef.core.base.BaseViewModel
import com.immotef.dashboardrepository.DashboardRepository
import com.immotef.dashboardrepository.data.InfectionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */


internal class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    coroutineUtils: CoroutineUtils)
    : BaseViewModel(coroutineUtils) {

    init {
        realodDashboardData()
    }

    val dashboardInfectionStateDataStream: LiveData<InfectionState>
        get() {
            return dashboardRepository.provideInfectedState()
                .asLiveData(coroutineUtils.io)
                .distinctUntilChanged()
        }


    private var job: Job? = null

    fun realodDashboardData() {
        job?.cancel()
        job = viewModelScope.launch(errorHandler) {
            showProgressProcessor.postValue(true)
            withContext(coroutineUtils.io) {
                dashboardRepository.reloadDashBoardData()
            }
            showProgressProcessor.postValue(false)
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

}