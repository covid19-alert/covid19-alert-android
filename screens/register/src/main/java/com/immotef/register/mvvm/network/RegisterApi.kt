package com.immotef.register.mvvm.network

import com.immotef.network.register
import retrofit2.http.Body
import retrofit2.http.POST

/**
 *
 */


internal interface RegisterApi {
    @POST(register)
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}