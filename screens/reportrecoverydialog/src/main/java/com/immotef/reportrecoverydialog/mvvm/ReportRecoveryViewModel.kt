package com.immotef.reportrecoverydialog.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.immotef.core.CoroutineUtils
import com.immotef.core.base.ActionLiveData
import com.immotef.core.base.BaseViewModel
import com.immotef.dashboardrepository.DashboardRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */


class ReportRecoveryViewModel(
    private val reportRecoveryUseCase: ReportRecoveryUseCase,
    private val dashboardRepository: DashboardRepository,
    coroutineUtils: CoroutineUtils)
    : BaseViewModel(coroutineUtils) {


    private val recoveryReportProcessor: ActionLiveData<Unit> = ActionLiveData()
    val recoveryReportSuccessfullyStream: LiveData<Unit> get() = recoveryReportProcessor


    fun reportRecovery() {
        showProgressProcessor.postValue(true)
        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                dashboardRepository.updateWithDashboardResponse(reportRecoveryUseCase.reportRecovery())
            }
            showProgressProcessor.postValue(false)
            recoveryReportProcessor.sendAction(Unit)
        }
    }
}