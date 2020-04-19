package com.immotef.coronavirusblockade

import android.app.backup.BackupAgentHelper
import android.app.backup.SharedPreferencesBackupHelper
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.immotef.scanjob.service.NotificationChannelProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 *
 */

private const val sharedPrefsName = "corono_prefs"
const val PREFS_BACKUP_KEY = "prefs"

val appModule = module {

    single<SharedPreferences> {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val key = MasterKeys.getOrCreate(keyGenParameterSpec)
            EncryptedSharedPreferences.create(
                sharedPrefsName, key, androidApplication(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            androidContext().getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
        }
    }
    single<NotificationChannelProvider> { NotificationChannelProviderImp(androidApplication()) }
}


class CoronaBackupAgent : BackupAgentHelper() {
    override fun onCreate() {
        // Allocate a helper and add it to the backup agent
        SharedPreferencesBackupHelper(this, sharedPrefsName).also {
            addHelper(PREFS_BACKUP_KEY, it)
        }
    }
}