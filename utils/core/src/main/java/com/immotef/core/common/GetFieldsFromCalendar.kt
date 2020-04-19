package com.immotef.core.common

import java.util.*

class GetFieldsFromCalendar(private val date: Date?) {
    var year: Int = 0
        private set
    var month: Int = 0
        private set
    var day: Int = 0
        private set


    var hour: Int = 0
    var minute: Int = 0

    init {
        val c = Calendar.getInstance()
        if (date != null) {
            c.time = date
        }
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
        hour = c.get(Calendar.HOUR_OF_DAY)
        minute = c.get(Calendar.MINUTE)
    }

    operator fun invoke(): GetFieldsFromCalendar {
        val c = Calendar.getInstance()
        if (date != null) {
            c.time = date
        }
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
        hour = c.get(Calendar.HOUR_OF_DAY)
        minute = c.get(Calendar.MINUTE)
        return this
    }
}
