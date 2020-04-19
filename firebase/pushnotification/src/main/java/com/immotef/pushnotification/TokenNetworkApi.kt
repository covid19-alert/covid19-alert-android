package com.immotef.pushnotification

import com.google.gson.annotations.SerializedName
import com.immotef.network.fcmToken
import retrofit2.http.Body
import retrofit2.http.POST

/**
 *
 */


internal interface TokenNetworkApi {
    @POST(fcmToken)
    suspend fun uploadToken(@Body tokenRequest: FcmTokenRequest)
}


internal data class FcmTokenRequest(@SerializedName("firebaseToken") val token: String)