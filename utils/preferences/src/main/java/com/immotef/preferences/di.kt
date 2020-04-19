package com.immotef.preferences

import android.app.backup.BackupManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 *
 */


val preferencesModule = module {
    single<PreferencesFacade> { PreferencesFacadeImp(get(), BackupManager(androidApplication())) }
}