package com.immotef.pushnotification

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.immotef.authorization.AuthorizationProvider
import com.immotef.authorization.AuthorizationSaver
import com.immotef.authorization.LoggedState
import com.immotef.core.CoroutineUtils
import com.immotef.core.delegate.BoundCoroutineScopeDelegate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */


internal interface FirebaseTokenManager {
    fun uploadToken(newToken: String)
}


internal class FirebaseTokenManagerImp(
    private val authorizationSaver: AuthorizationSaver,
    private val authorizationProvider: AuthorizationProvider,
    private val tokenNetworkApi: TokenNetworkApi,
    private val coroutineUtils: CoroutineUtils
) : FirebaseTokenManager {

    private val scope by BoundCoroutineScopeDelegate()
    private var token: String? = null

    init {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                token = task.result?.token
                scope.launch {
                    if (token != null) {
                        authorizationSaver.saveFcmToken(token!!)
                    }
                }
            })
    }


    override fun uploadToken(newToken: String) {
        scope.launch {
            withContext(coroutineUtils.io) {
                authorizationSaver.saveFcmToken(newToken)
                if (authorizationProvider.isLogged() is LoggedState) {
                    tokenNetworkApi.uploadToken(FcmTokenRequest(newToken))
                }
            }
        }
    }

}