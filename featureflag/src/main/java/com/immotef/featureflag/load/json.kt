package com.immotef.featureflag.load

import com.google.gson.annotations.SerializedName


/**
 *
 */


data class FeatureFlagJson(
    @SerializedName("shouldCollectLocation")
    val shouldCollectLocation: Boolean,
    @SerializedName("shouldUploadDataAllTheTime")
    val shouldUploadDataAllTheTime: Boolean,
    @SerializedName("showReportButton")
    val showReportButton: Boolean,
    @SerializedName("displayShareButton")
    val showShareButton: Boolean,
    @SerializedName("onboarding")
    val showOnboarding: Boolean
)