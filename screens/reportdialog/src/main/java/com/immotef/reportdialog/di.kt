package com.immotef.reportdialog

import com.immotef.network.getApi
import com.immotef.reportdialog.mvvm.ReportDialogInfectionViewModel
import com.immotef.reportdialog.mvvm.ReportInfectionUseCaseImp
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *
 */


val infectionDialogModule = module {
    viewModel {
        ReportDialogInfectionViewModel(
            ReportInfectionUseCaseImp(getApi(), get()),
            get(), get()
        )
    }
}