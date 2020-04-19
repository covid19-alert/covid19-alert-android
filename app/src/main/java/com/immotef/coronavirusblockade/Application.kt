package com.immotef.coronavirusblockade

import android.app.Application
import com.immotef.authorization.authorizationModule
import com.immotef.beacon.BEACON_WAY
import com.immotef.beacon.beaconModule
import com.immotef.beaconscanner.btScannerModule
import com.immotef.core.coreModules
import com.immotef.dashboard.dashbordsModules
import com.immotef.dashboardrepository.dashboardRepositoryModule
import com.immotef.db.dbModule
import com.immotef.dynamic_links.deepLinkModule
import com.immotef.featureflag.featureFlagModule
import com.immotef.ibeaconpretender.ibeaconPretenderModule
import com.immotef.imageloading.imageModule
import com.immotef.infectedrepository.infectedRepositoryModule

import com.immotef.meetrepository.meetRepositoryModule
import com.immotef.network.networkingModule
import com.immotef.onboarding.onboardingModule
import com.immotef.preferences.preferencesModule
import com.immotef.pushnotification.fcmModule
import com.immotef.register.registerModules
import com.immotef.reportdialog.infectionDialogModule
import com.immotef.reportinfection.reportFlowModule
import com.immotef.scanjob.scanJobModules
import com.immotef.uniquemeetrepository.uniqueMeetModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 *
 */
private const val UUID_CONST = "43DB3082-A889-4510-902A-E99E5EDB9504"

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            val localTraceOnly = true
            modules(
                listOf(
                    appModule,
                    preferencesModule, ibeaconPretenderModule,
                    btScannerModule, beaconModule(UUID_CONST), meetRepositoryModule
                    , authorizationModule, networkingModule(BuildConfig.API_URL, BuildConfig.DEBUG),
                    dbModule, onboardingModule, imageModule, reportFlowModule, fcmModule, deepLinkModule,
                    infectionDialogModule, featureFlagModule, dashboardRepositoryModule(localTraceOnly), uniqueMeetModule, infectedRepositoryModule
                ) + coreModules + registerModules + dashbordsModules + scanJobModules(BEACON_WAY)
            )
        }
    }
}