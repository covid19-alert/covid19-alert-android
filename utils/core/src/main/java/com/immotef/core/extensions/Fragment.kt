package com.immotef.core.extensions

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController


/**
 *
 */

fun Fragment.getColor(@ColorRes res: Int) = ContextCompat.getColor(requireContext(), res)
inline fun Fragment.animateColor(colorFrom: Int, colorTo: Int, crossinline func: (Int) -> Unit): ValueAnimator {
    val colorAnimation =
        ValueAnimator.ofObject(ArgbEvaluator(), ContextCompat.getColor(requireContext(), colorFrom), ContextCompat.getColor(requireContext(), colorTo))
    colorAnimation.duration = 1000
    colorAnimation.interpolator = AccelerateDecelerateInterpolator()
    colorAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    colorAnimation.start()
    return colorAnimation
}

inline fun Fragment.animateInt(from: Int, to: Int, crossinline func: (Int) -> Unit): ValueAnimator {
    val intAnimation =
        ValueAnimator.ofInt(from, to)
    intAnimation.duration = 1000
    intAnimation.addUpdateListener { animation ->
        func(animation.animatedValue as Int)
    }
    intAnimation.interpolator = AccelerateDecelerateInterpolator()
    intAnimation.start()
    return intAnimation
}

fun Fragment.startCameraActivity(requestCode: Int = 0, uri: Uri) {
    startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
    }, requestCode)
}

fun Fragment.startGalleryActivity(requestCode: Int = 0) {
    startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode)
}

fun <T> Fragment.findReturnLiveData(key: String): LiveData<T>? = findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData(key)
fun  Fragment.putReturnValueToLiveData(key: String,value:Any) = findNavController().previousBackStackEntry?.savedStateHandle?.set(key,value)
