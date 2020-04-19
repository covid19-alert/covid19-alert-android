package com.immotef.register.mvvm.network

import com.google.gson.annotations.SerializedName

/**
 *
 */


internal data class RegisterRequest(@SerializedName("deviceUUID") val uuid: String,@SerializedName("firebaseToken")val  fcmToken:String?)

internal data class RegisterResponse(@SerializedName("major") val major: Long,
                                     @SerializedName("minor") val minor: Long,
                                     @SerializedName("authToken") val token: String?) {
    fun token(): String = if ((token ?: "").isNotBlank()) token!! else "$major:$minor"
}