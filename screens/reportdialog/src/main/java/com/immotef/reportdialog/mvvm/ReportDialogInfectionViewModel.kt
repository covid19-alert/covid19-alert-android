package com.immotef.reportdialog.mvvm

import androidx.lifecycle.*
import com.immotef.core.CoroutineUtils
import com.immotef.core.errors.ErrorWrapper
import com.immotef.dashboardrepository.DashboardRepository
import com.immotef.reportdialog.data.InfectionData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 *
 */
interface CheckIsCodeAlreadyUsed {
    fun check(text: String): Boolean {

        return text == "Code was already used"
    }
}

class ReportDialogInfectionViewModel(
    private val reportInfectionUseCase: ReportInfectionUseCase,
    private val dashboardRepository: DashboardRepository,
    private val coroutineUtils: CoroutineUtils,
    private val checker: CheckIsCodeAlreadyUsed = object : CheckIsCodeAlreadyUsed {})
    : ViewModel() {


    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        val wrapper = coroutineUtils.produce(throwable)
        errorProcessor.postValue(wrapper)
        if (throwable is HttpException && throwable.code() == 500 && checker.check(wrapper.text)) {
            viewModelScope.launch {
                reportInfectionUseCase.clearInfectionData()
            }
        }
        showProgressProcessor.postValue(false)
    }
    private val errorProcessor: MutableLiveData<ErrorWrapper> = MutableLiveData()
    val errorWrapperStream: LiveData<ErrorWrapper> get() = errorProcessor

    private val showProgressProcessor: MutableLiveData<Boolean> = MutableLiveData(false)

    val showProgressStream: LiveData<Boolean>
        get() = showProgressProcessor.distinctUntilChanged()


    private val infectionReportedProcessor: BroadcastChannel<Unit> = BroadcastChannel(1)
    val infectionReportedStream: LiveData<Unit> get() = infectionReportedProcessor.asFlow().asLiveData(viewModelScope.coroutineContext)



    fun confirmInfection(infectionData: InfectionData) {
        viewModelScope.launch(errorHandler) {
            showProgressProcessor.postValue(true)
            withContext(coroutineUtils.io) {

                dashboardRepository.updateWithDashboardResponse(reportInfectionUseCase.reportInfection(infectionData))

            }
            showProgressProcessor.postValue(false)
            infectionReportedProcessor.send(Unit)
        }
    }

    fun denyInfection() {
        viewModelScope.launch {
            reportInfectionUseCase.clearInfectionData()
        }
    }
}