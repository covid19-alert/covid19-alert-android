package com.immotef.core.extensions

import android.os.Build
import android.provider.Settings.Global.getString
import android.text.Html
import android.widget.TextView

/**
 *
 */



fun TextView.fromHtml(id:Int){
   text =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(resources.getString(id), Html.FROM_HTML_MODE_LEGACY)
    }else{
        Html.fromHtml(resources.getString(id))
    }
}