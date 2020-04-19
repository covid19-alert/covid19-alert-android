package com.immotef.core.extensions

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

fun dateTimeFormat() = SimpleDateFormat("dd MMMM yyyy '-' HH:mm", Locale.getDefault())

fun relativeTimeSpanString(date: Date): CharSequence =
    DateUtils.getRelativeTimeSpanString(date.time, Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
