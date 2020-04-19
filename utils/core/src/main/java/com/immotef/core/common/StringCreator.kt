package com.immotef.core.common

import android.content.Context
import android.content.res.Resources
import android.text.format.DateFormat
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */

interface StringCreator {
    fun createString(id: Int): String
    fun createString(id: Int, text: String): String
    fun createDate(date: Date): String
    fun crateDateTime(date: Date): String
}

internal class StringCreatorImp(
    private val context: Context,
    private val resources: Resources = context.resources
) : StringCreator {
    override fun createString(id: Int) = resources.getString(id)

    override fun createString(id: Int, text: String) = resources.getString(id, text)

    override fun createDate(date: Date): String {
        return SimpleDateFormat("dd MMMM',' yyyy'|'HH:mm", Locale.getDefault()).format(date)

    }

    override fun crateDateTime(date: Date): String {
        val timeFormat: Format = DateFormat.getTimeFormat(context)
        val dateFormat: Format = DateFormat.getDateFormat(context)
        return "${timeFormat.format(date)} - ${dateFormat.format(date)}"
    }
}