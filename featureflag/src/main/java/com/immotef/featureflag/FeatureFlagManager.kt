package com.immotef.featureflag

import android.content.Context
import com.immotef.core.CoroutineUtils
import com.immotef.featureflag.load.SaveFeatureFlagUseCase
import com.immotef.preferences.PreferencesFacade
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */


interface FeatureFlagManager {
    suspend fun init()
    suspend fun getFeatureFlag(flag: FeatureFlag): Boolean
}


internal class FeatureFlagManagerImp(
    private val preferencesFacade: PreferencesFacade,
    private val coroutineUtils: CoroutineUtils,
    private val saveFeatureFlagUseCase: SaveFeatureFlagUseCase,
    private val flagsToCheckForExist: List<FeatureFlag> = listOf(FeatureFlag.ShowOnboarding)
) : FeatureFlagManager {
    init {
        coroutineUtils.globalScope.launch {
            init()
        }
    }

    private suspend fun startConditions() {

        if (flagsToCheckForExist.map { !preferencesFacade.contains(it.name) }.fold(false, { last, new -> last || new })) {
            withContext(coroutineUtils.io) {
                saveFeatureFlagUseCase.saveFeatureFlag()
            }
        }
    }

    override suspend fun init() {
        startConditions()
    }

    override suspend fun getFeatureFlag(flag: FeatureFlag): Boolean = preferencesFacade.retrieveBoolean(flag.name)

}


fun Context.readFileText(fileName: String): String {
    return assets.open(fileName).bufferedReader().use { it.readText() }
}