package com.immotef.reportdialog.mvvm.network

import com.google.gson.annotations.SerializedName

/**
 *
 */


data class ReportInfectionRequest(@SerializedName("infectionValidationKey") val id: String)