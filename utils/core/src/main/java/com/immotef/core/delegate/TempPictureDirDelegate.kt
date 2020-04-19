package com.immotef.core.delegate

import android.app.Activity
import android.content.Context
import android.os.Environment
import androidx.fragment.app.Fragment
import java.io.File
import kotlin.reflect.KProperty

class TempPictureDirDelegate {
    operator fun getValue(thisRef: Fragment, property: KProperty<*>): File? {
        return createFille(thisRef.requireContext())
    }

    private fun createFille(context: Context) =context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    operator fun getValue(thisRef: Activity, property: KProperty<*>): File? {
        return createFille(thisRef)
    }
}