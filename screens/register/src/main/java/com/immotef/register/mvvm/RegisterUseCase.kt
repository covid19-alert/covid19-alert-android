package com.immotef.register.mvvm

import com.immotef.authorization.AuthorizationData
import com.immotef.authorization.AuthorizationProvider
import com.immotef.authorization.AuthorizationSaver
import com.immotef.beacon.BeaconSetup
import com.immotef.register.mvvm.network.RegisterApi
import com.immotef.register.mvvm.network.RegisterRequest

/**
 *
 */


internal interface RegisterUseCase {
    suspend fun register()
}


internal class RegisterUseCaseImp(
    private val registerApi: RegisterApi,
    private val authorizationSaver: AuthorizationSaver,
    private val authorizationProvider: AuthorizationProvider,
    private val beaconSetup: BeaconSetup)
    : RegisterUseCase {
    override suspend fun register() {
        val response = registerApi.register(RegisterRequest(authorizationProvider.provideUUID(), authorizationProvider.provideFcmToken()))
        beaconSetup.setMajorMinor(response.major.toInt(), response.minor.toInt())
        authorizationSaver.saveAuthData(AuthorizationData(response.token()))
    }
}
