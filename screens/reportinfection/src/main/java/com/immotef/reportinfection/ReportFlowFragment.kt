package com.immotef.reportinfection

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.immotef.core.extensions.observe
import com.immotef.network.getApi
import com.immotef.reportinfection.mvvm.FlowViewModel
import com.immotef.reportinfection.report.mvvm.ReportInfectionUseCase
import com.immotef.reportinfection.report.mvvm.ReportInfectionUseCaseImp
import com.immotef.reportinfection.report.mvvm.ReportInfectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

/**
 *
 */


class ReportFlowFragment : Fragment(R.layout.report_flow_fragment) {

    private val viewModel: FlowViewModel by viewModel()
    private val reportInfectionViewModel: ReportInfectionViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.report_id_key), -1L) ?: -1L
        val timeStamp = arguments?.getLong(getString(R.string.report_time_key), -1L) ?: -1L
        if (id != -1L && timeStamp != -1L) {
            reportInfectionViewModel.updateDate(timeStamp)
        }
        reportInfectionViewModel.infectionReportedStream.observe(viewLifecycleOwner, Observer {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.dashboard_successfully_reported_title)
                .setMessage(R.string.dashboard_successfully_reported_message)
                .setCancelable(false)
                .setNegativeButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().apply {
                        popBackStack()
                    }
                }
                .show()
        })
        observe(viewModel.goBackStream) {
            findNavController().popBackStack()
        }
    }
}


val reportFlowModule = module {
    viewModel { FlowViewModel(get()) }
    viewModel { ReportInfectionViewModel(get(), get(), get()) }
    factory<ReportInfectionUseCase> { ReportInfectionUseCaseImp(getApi(), get()) }
}