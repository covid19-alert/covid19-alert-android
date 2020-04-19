package com.immotef.reportinfection.report.mvvm.network

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 *
 */


data class ReportInfectionRequest(@SerializedName("testedAt") val date: Date)