package com.immotef.core.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 *
 */



fun Activity.openSettingsPage(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Activity.createShareIntent(text: String, title: String, chooser: String) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, title)
        type = "text/plain"
    }
    startActivity(Intent.createChooser(shareIntent, chooser))
}