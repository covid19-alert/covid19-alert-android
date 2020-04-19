package com.immotef.reportinfection.report

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.immotef.core.common.GetFieldsFromCalendar
import com.immotef.core.delegate.TempPictureDirDelegate
import com.immotef.core.extensions.*
import com.immotef.core.permission.AppPermission
import com.immotef.core.permission.handlePermission
import com.immotef.core.permission.requestPermission
import com.immotef.imageloading.ImageLoader
import com.immotef.reportinfection.R
import com.immotef.reportinfection.mvvm.FlowViewModel
import com.immotef.reportinfection.report.mvvm.ReportInfectionViewModel
import kotlinx.android.synthetic.main.fragment_report_infection.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File
import java.util.*

/**
 *
 */
private const val CAMERA_REQUEST_CODE = 0
private const val GALLERY_REQUEST_CODE = 1


internal class ReportInfectionFragment : Fragment(R.layout.fragment_report_infection) {
    private val cameraPermission = AppPermission(Manifest.permission.CAMERA, 128)
    private val tempPictureDir by TempPictureDirDelegate()
    private var fileProviderUri: Uri? = null

    private val imageLoader: ImageLoader by inject()
    private val flowViewModel: FlowViewModel by lazy {
        requireParentFragment().requireParentFragment().getViewModel<FlowViewModel>()
    }
    private val viewModel: ReportInfectionViewModel by lazy {
        requireParentFragment().requireParentFragment().getViewModel<ReportInfectionViewModel>()
    }

    private val onGranted: (AppPermission) -> Unit = {
        fileProviderUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", photoFile())
        startCameraActivity(CAMERA_REQUEST_CODE, fileProviderUri!!)
    }

    private val onRational: (AppPermission) -> Unit = {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.report_camera_rational_title)
            .setMessage(R.string.report_camera_rational_message)
            .setPositiveButton(R.string.ok) { d, _ ->
                d.dismiss()
                requestPermission(it.permissionName, it.requestCode)
            }
            .show()
    }

    private fun photoFile() = File.createTempFile("image_", ".jpg", tempPictureDir)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pictureFrameLayout.setOnClickListener {
            showPictureChooseDialog()
        }

        viewModel.getUri()?.also {
            imageLoader.loadImageWithRoundedCorners(it, reportImageView, resources.getDimension(R.dimen.corner_radius).toInt())
        }

        viewModel.getDateString()?.also {
            reportDateText.text = it
        }
        reportDateText.isEnabled = viewModel.shouldDateBeEnabled()
        reportDateText.setOnClickListener {
            openDatePickerWithDate(Date(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                viewModel.updateDate(dayOfMonth, month, year)
                val text = "$dayOfMonth/$month/$year"
                reportDateText.text = text
            }).show()
        }
        handleRefreshLayout()
        handleConfirmButton()

        reportCancelButton.setOnClickListener { flowViewModel.navigateBack() }
        reportGoBackButton.setOnClickListener { flowViewModel.navigateBack() }

        observe(viewModel.errorWrapperStream) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.error_occured)
                .setMessage(it.text)
                .setPositiveButton(R.string.ok) { d, w ->
                    d.dismiss()
                    it.action?.invoke(this)
                }
                .show()
        }
    }

    private fun handleConfirmButton() {
        observe(viewModel.canUploadStream) {
            reportConfirmButton.isEnabled = it
        }
        reportConfirmButton.setOnClickListener {
            viewModel.reportInfection()
        }
    }

    private fun handleRefreshLayout() {
        reportRefreshLayout.isEnabled = false
        observe(viewModel.showProgressStream) {
            reportRefreshLayout.isRefreshing = it
        }
    }

    private fun showPictureChooseDialog() {
        AlertDialog.Builder(requireContext())
            .setDialogArrayAdapter(DialogArrayAdapter(requireContext(), android.R.layout.simple_list_item_1).apply {
                add(DialogListAction(R.string.report_photo) {
                    handlePermission(cameraPermission, onGranted, onRational)
                })
                add(DialogListAction(R.string.report_gallery) { startGalleryActivity(GALLERY_REQUEST_CODE) })
                if (flowViewModel.uri != null) {
                    add(DialogListAction(R.string.report_modify_current) { navigateToBlurr() })
                }
            })
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> fileProviderUri?.let {
                    flowViewModel.uri = it
                }
                GALLERY_REQUEST_CODE -> data?.data?.let {
                    flowViewModel.uri = it
                }
            }
            navigateToBlurr()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        handlePermission(cameraPermission, onGranted, {})
    }


    private fun navigateToBlurr() {
        findNavController().navigate(R.id.action_reportInfectionFragment_to_blurrImageFragment)
    }
}


fun Fragment.openDatePickerWithDate(date: Date = Date(), lister: DatePickerDialog.OnDateSetListener): DatePickerDialog {
    val getFieldsFromCalendar = GetFieldsFromCalendar(date).invoke()
    val year = getFieldsFromCalendar.year
    val month = getFieldsFromCalendar.month
    val day = getFieldsFromCalendar.day
    return DatePickerDialog(requireContext(), lister, year, month, day)
}
