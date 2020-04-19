package com.immotef.featureflag.load

import android.content.Context
import com.google.gson.Gson
import com.immotef.featureflag.FeatureFlag
import com.immotef.featureflag.readFileText
import com.immotef.preferences.PreferencesFacade

/**
 *
 */


interface SaveFeatureFlagUseCase {
    suspend fun saveFeatureFlag()
}

internal class SaveFeatureFlagUseCaseImp(
    private val context: Context,
    private val gson: Gson,
    private val preferencesFacade: PreferencesFacade) : SaveFeatureFlagUseCase {

    override suspend fun saveFeatureFlag() {
        val featureFlag = gson.fromJson(context.readFileText("featureFlag.json"), FeatureFlagJson::class.java)
        preferencesFacade.saveBoolean(featureFlag.showReportButton, FeatureFlag.ShowReportButton.name)
        preferencesFacade.saveBoolean(featureFlag.showReportButton, FeatureFlag.DisplayShareButton.name)
        preferencesFacade.saveBoolean(featureFlag.showOnboarding, FeatureFlag.ShowOnboarding.name)
    }

}