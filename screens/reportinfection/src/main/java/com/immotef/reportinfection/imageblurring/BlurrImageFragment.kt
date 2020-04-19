package com.immotef.reportinfection.imageblurring

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.immotef.core.delegate.TempPictureDirDelegate
import com.immotef.core.extensions.writeBitmap
import com.immotef.imageloading.ImageLoader
import com.immotef.reportinfection.R
import com.immotef.reportinfection.mvvm.FlowViewModel
import com.immotef.reportinfection.report.mvvm.ReportInfectionViewModel
import kotlinx.android.synthetic.main.fragment_image_change.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File


/**
 *
 */


internal class BlurrImageFragment : Fragment(R.layout.fragment_image_change), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private val tempPictureDir by TempPictureDirDelegate()
    private fun photoFile() = File.createTempFile("image_", ".jpg", tempPictureDir)

    private val imageLoader: ImageLoader by inject()
    private val flowViewModel: FlowViewModel by lazy {
        requireParentFragment().requireParentFragment().getViewModel<FlowViewModel>()
    }
    private val viewModel: ReportInfectionViewModel by lazy {
        requireParentFragment().requireParentFragment().getViewModel<ReportInfectionViewModel>()
    }

    private var croppedBitmap: Bitmap? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        save.setOnClickListener(this)
        undo.setOnClickListener(this)
        clear.setOnClickListener(this)
        red.setOnSeekBarChangeListener(this)
        green.setOnSeekBarChangeListener(this)
        blue.setOnSeekBarChangeListener(this)
        tolerance.setOnSeekBarChangeListener(this)
        width.setOnSeekBarChangeListener(this)
        normal.setOnClickListener(this)
        emboss.setOnClickListener(this)
        blur.setOnClickListener(this)

//
        cropImage.setImageUriAsync(flowViewModel.uri)
        cropImage.setOnCropImageCompleteListener { _, result ->
            result.bitmap?.apply {
                imageLoader.loadImageCenterInside(writeImage(this), finger)
                cropLayout.visibility = View.GONE
                drawLayout.visibility = View.VISIBLE
            }
        }
        cropButton.setOnClickListener {
            cropImage.getCroppedImageAsync()
        }

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar?.id == red.id || seekBar?.id == green.id || seekBar?.id == blue.id) {
            val r = red.progress
            val g = green.progress
            val b = blue.progress
            val color = Color.argb(255, r, g, b)
            finger.strokeColor = color
            colorPreview.setBackgroundColor(color)
        } else if (seekBar?.id == tolerance.id) {
            finger.touchTolerance = progress.toFloat()
        } else if (seekBar?.id == width.id) {
            finger.strokeWidth = progress.toFloat()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            undo -> finger.undo()
            clear -> finger.clear()
            save -> {
                finger.drawable?.toBitmap()?.let {
                    viewModel.updateImageUri(writeImage(it))
                    findNavController().navigateUp()
                }

            }
            emboss -> finger.emboss()
            blur -> finger.blur()
            normal -> finger.normal()
        }
    }

    private fun writeImage(it: Bitmap): Uri {
        val photoFile = photoFile()
        photoFile.writeBitmap(it, Bitmap.CompressFormat.JPEG, 100)
        return getUriFromFile(photoFile)

    }

    private fun getUriFromFile(file: File) = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }


}