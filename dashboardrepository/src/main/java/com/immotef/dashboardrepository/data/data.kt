package com.immotef.dashboardrepository.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 *
 */


data class DashboardData(val peoopleYouHaveMet: Int, val infectedPeople: Int, val risk: Int, val isInfected: Boolean, val isRecovered: Boolean)

@Parcelize
data class DashboardResponse(
    @SerializedName("items") val items: List<MeetItem>?,
    @SerializedName("riskLevel") val riskLevel: Int,
    @SerializedName("numberOfInfectedMet") val numberOfInfectedMet: Int,
    @SerializedName("reportedSelfInfection") val isInfected: Boolean,
    @SerializedName("reportedRecovered") val isRecovered: Boolean,
    @SerializedName("metInfectedIds") val infectedList: List<String>? = null
) : Parcelable

@Parcelize
data class MeetItem(@SerializedName("seenUserId") val majorMinor: String, @SerializedName("isInfected") val isInfected: Boolean) : Parcelable


