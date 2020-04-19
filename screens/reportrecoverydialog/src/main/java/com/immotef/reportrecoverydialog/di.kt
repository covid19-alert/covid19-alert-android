package com.immotef.reportrecoverydialog

import com.immotef.network.getApi
import com.immotef.reportrecoverydialog.mvvm.ReportRecoveryUseCaseImp
import com.immotef.reportrecoverydialog.mvvm.ReportRecoveryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *
 */


val reportRecoveryModule = module {
    viewModel {
        ReportRecoveryViewModel(
            ReportRecoveryUseCaseImp(getApi()),
            get(), get()
        )
    }
}