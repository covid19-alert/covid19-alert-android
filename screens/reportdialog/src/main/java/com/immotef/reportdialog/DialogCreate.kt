package com.immotef.reportdialog

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.immotef.core.extensions.observe
import com.immotef.reportdialog.data.InfectionData
import com.immotef.reportdialog.mvvm.ReportDialogInfectionViewModel

/**
 *
 */
fun AppCompatActivity.registerInfectionStuff(viewModel: ReportDialogInfectionViewModel) {


    observe(viewModel.infectionReportedStream) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dashboard_successfully_reported_title)
            .setMessage(R.string.dashboard_successfully_reported_message)
            .setCancelable(false)
            .setNegativeButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    observe(viewModel.errorWrapperStream) {
        AlertDialog.Builder(this)
            .setTitle(R.string.error_occured)
            .setMessage(it.text)
            .setPositiveButton(R.string.ok) { d, w ->
                d.dismiss()
            }
            .show()
    }
}

fun AppCompatActivity.reportInfectionData(viewModel: ReportDialogInfectionViewModel,
                                          id: String) {


    AlertDialog.Builder(this)
        .setTitle(R.string.dialog_report_infection_title)
        .setMessage(R.string.dialog_report_infection_message)
        .setPositiveButton(R.string.dialog_report_infection_confirm) { dialog, _ ->
            viewModel.confirmInfection(InfectionData(id))
            dialog.dismiss()
        }
        .setNegativeButton(R.string.dialog_report_infection_decline) { dialog, _ ->
            viewModel.denyInfection()
            dialog.dismiss()
        }.show()


}

fun Fragment.reportInfectionData(viewModel: ReportDialogInfectionViewModel,
                                 id: String) {


    AlertDialog.Builder(requireContext())
        .setTitle(R.string.dialog_report_infection_title)
        .setMessage(R.string.dialog_report_infection_message)
        .setPositiveButton(R.string.dialog_report_infection_confirm) { dialog, _ ->
            viewModel.confirmInfection(InfectionData(id))
            dialog.dismiss()
        }
        .setNegativeButton(R.string.dialog_report_infection_decline) { dialog, _ ->
            viewModel.denyInfection()
            dialog.dismiss()
        }.show()
}


fun Fragment.registerInfectionDialog(viewModel: ReportDialogInfectionViewModel,
                                     id: String) {


    observe(viewModel.infectionReportedStream) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dashboard_successfully_reported_title)
            .setMessage(R.string.dashboard_successfully_reported_message)
            .setCancelable(false)
            .setNegativeButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    observe(viewModel.errorWrapperStream) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error_occured)
            .setMessage(it.text)
            .setPositiveButton(R.string.ok) { d, w ->
                d.dismiss()
            }
            .show()
    }
}