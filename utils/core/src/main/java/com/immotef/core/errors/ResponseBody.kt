package com.immotef.core.errors

import com.google.gson.annotations.SerializedName


/**
 *
 */


data class ResponseErrorBody(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("errorCode")
    val errorCode: Int
)