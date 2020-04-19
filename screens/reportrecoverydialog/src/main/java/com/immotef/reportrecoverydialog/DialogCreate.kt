package com.immotef.reportrecoverydialog

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.immotef.core.extensions.observe
import com.immotef.reportrecoverydialog.mvvm.ReportRecoveryViewModel

/**
 *
 */


fun Fragment.reportRecovery(viewModel: ReportRecoveryViewModel) {
    AlertDialog.Builder(requireContext())
        .setTitle(R.string.dialog_sure_to_be_recovery_title)
        .setMessage(R.string.dialog_sure_to_be_recovery_message)
        .setCancelable(false)
        .setPositiveButton(R.string.dialog_sure_to_be_recovery_confirm) { dialog, _ ->
            viewModel.reportRecovery()
            dialog.dismiss()
        }
        .setNegativeButton(R.string.dialog_sure_to_be_recovery_deny) { dialog, _ ->

            dialog.dismiss()
        }.show()
}

fun Fragment.registerRecoveryDialog(viewModel: ReportRecoveryViewModel,
                                    successFunc: () -> Unit = {}) {


    observe(viewModel.recoveryReportSuccessfullyStream) { report ->
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_success_report_recovery_title)
            .setMessage(R.string.dialog_success_report_recovery_message)
            .setCancelable(false)
            .setNegativeButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                successFunc()
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