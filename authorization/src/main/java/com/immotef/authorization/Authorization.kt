package com.immotef.authorization

import com.immotef.featureflag.FeatureFlag
import com.immotef.featureflag.FeatureFlagManager
import com.immotef.preferences.PreferencesFacade
import java.util.*

/**
 *
 */
private const val TOKEN_KEY = "token_key"
private const val BOARDING_KEY = "onboaring_key"
private const val FCM_TOKEN_KEY = "fcm_token_key"
private const val UUID_KEY = "uuid_token_key"

sealed class LoginState
object LoggedState : LoginState()
object NotLoggedState : LoginState()
object NeedOnBoarding : LoginState()

interface AuthorizationSaver {
    suspend fun saveAuthData(data: AuthorizationData)
    suspend fun saveOnBoardingFinished()
    suspend fun saveFcmToken(token: String)
    suspend fun logout()
}


interface AuthorizationProvider {
    suspend fun provideToken(): String?
    suspend fun provideFcmToken(): String?
    suspend fun isLogged(): LoginState
    suspend fun provideUUID(): String
}

internal class AuthorizationManager(
    private val preferencesFacade: PreferencesFacade,
    private val featureFlag: FeatureFlagManager,
    private val tokenKey: String = TOKEN_KEY,
    private val onBoadringKey: String = BOARDING_KEY,
    private val fcmTokenKey: String = FCM_TOKEN_KEY,
    private val uuidKey: String = UUID_KEY
) : AuthorizationSaver,
    AuthorizationProvider {

    private var token: String? = null

    private var fcmToken: String? = null

    override suspend fun saveAuthData(data: AuthorizationData) {
        token = data.token

        preferencesFacade.saveString(text = data.token, key = tokenKey)
    }

    override suspend fun saveOnBoardingFinished() {
        preferencesFacade.saveBoolean(true, onBoadringKey)
    }

    override suspend fun saveFcmToken(token: String) {
        fcmToken = token
        preferencesFacade.saveString(token, fcmTokenKey)

    }

    override suspend fun logout() {
        preferencesFacade.remove(tokenKey)
        token = null
    }

    private var uuid: String? = null
    override suspend fun provideUUID(): String {
        return uuid ?: (preferencesFacade.retrieveString(uuidKey).let {
            if (it == null) {
                val newUUID = UUID.randomUUID().toString()
                uuid = newUUID
                preferencesFacade.saveString(newUUID, uuidKey)
            } else {
                uuid = it
            }
            uuid!!
        })
    }

    override suspend fun provideFcmToken(): String? {
        if (fcmToken == null || fcmToken?.isBlank() == true) {
            fcmToken = preferencesFacade.retrieveString(fcmTokenKey)
        }
        return fcmToken
    }


    override suspend fun provideToken(): String? {
        if (token == null || token?.isBlank() == true) {
            token = preferencesFacade.retrieveString(tokenKey)
        }
        return token
    }

    override suspend fun isLogged(): LoginState {
        if (token == null || token?.isBlank() == true) {
            token = preferencesFacade.retrieveString(tokenKey)
        }
        val onboaring = !preferencesFacade.retrieveBoolean(onBoadringKey)

        return when {
            token?.isNotBlank() == true -> LoggedState
            onboaring && featureFlag.getFeatureFlag(FeatureFlag.ShowOnboarding) -> NeedOnBoarding
            else -> NotLoggedState
        }
    }
}

