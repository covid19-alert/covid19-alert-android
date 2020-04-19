package com.immotef.pushnotification

import com.google.firebase.messaging.FirebaseMessagingService
import org.koin.android.ext.android.inject

class FirebaseTokenService : FirebaseMessagingService() {

    private val firebaseTokenManager:FirebaseTokenManager by inject()

    override fun onNewToken(p0: String) {
        firebaseTokenManager.uploadToken(p0)
    }
}

