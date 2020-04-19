package com.immotef.core.extensions

import android.content.Context
import android.content.DialogInterface
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

/**
 *
 */
class DialogArrayAdapter(context: Context, @LayoutRes resource: Int) : ArrayAdapter<DialogArrayAdapter.DialogListAction>(context, resource),
    DialogInterface.OnClickListener {

    override fun onClick(dialog: DialogInterface?, which: Int) {
        getItem(which)?.apply { action() }
    }

    inner class DialogListAction(@StringRes val resource: Int, val action: () -> Unit) {
        override fun toString(): String = context.getString(resource)
    }
}



fun AlertDialog.Builder.setDialogArrayAdapter(adapter: DialogArrayAdapter): AlertDialog.Builder = setAdapter(adapter, adapter)