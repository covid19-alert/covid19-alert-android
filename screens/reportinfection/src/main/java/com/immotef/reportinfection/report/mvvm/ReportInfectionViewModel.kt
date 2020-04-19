package com.immotef.reportinfection.report.mvvm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.immotef.core.CoroutineUtils
import com.immotef.core.base.BaseViewModel
import com.immotef.core.common.Validator
import com.immotef.dashboardrepository.DashboardRepository
import com.immotef.reportinfection.InfectionData
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 *
 */


internal class ReportInfectionViewModel(
    private val reportInfectionUseCase: ReportInfectionUseCase,
    private val dashboardRepository: DashboardRepository,
    coroutineUtils: CoroutineUtils,
    private val validator: Validator<InfectionData> = InfectionDataValidator()
) : BaseViewModel(coroutineUtils) {

    private val infectionReportedProcessor: MutableLiveData<Unit> = MutableLiveData()
    val infectionReportedStream: LiveData<Unit> get() = infectionReportedProcessor

    private var data: InfectionData = InfectionData(-1, -1, -1, null)
        set(value) {
            field = value
            canUploadProcessor.postValue(validator.validate(value))
        }

    private val canUploadProcessor: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val canUploadStream: LiveData<Boolean> get() = canUploadProcessor.distinctUntilChanged()

    fun updateImageUri(uri: Uri?) {
        data = data.copy(imageUri = uri)
    }

    fun updateDate(day: Int, month: Int, year: Int) {
        data = data.copy(year = year, month = month, day = day)
    }

    fun reportInfection() {
        showProgressProcessor.postValue(true)
        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                dashboardRepository.updateWithDashboardResponse(reportInfectionUseCase.reportInfection(data))
            }
            showProgressProcessor.postValue(false)
            infectionReportedProcessor.postValue(Unit)
        }
    }

    fun getUri(): Uri? = data.imageUri
    fun getDateString(): String? = if (data.day > 0) {
        "${data.day}/${data.month}/${data.year}"
    } else null

    private var shouldDateBeEnabled = true
    fun updateDate(timeStamp: Long) {
        val c = Calendar.getInstance()
        c.timeInMillis = timeStamp
        shouldDateBeEnabled = false
        data = data.copy(year = c[Calendar.YEAR], month = c[Calendar.MONTH], day = c[Calendar.DAY_OF_MONTH])
    }

    fun shouldDateBeEnabled() = shouldDateBeEnabled
}

internal class InfectionDataValidator : Validator<InfectionData> {
    override fun validate(t: InfectionData): Boolean = when {
        t.year < 0 -> false
        t.month < 0 -> false
        t.day < 0 -> false
//        t.imageUri == null -> false
        else -> true
    }
}