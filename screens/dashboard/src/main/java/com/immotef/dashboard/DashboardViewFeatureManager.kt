package com.immotef.dashboard

import android.view.View
import com.immotef.core.CoroutineUtils
import com.immotef.core.extensions.setVisibleOrGone
import com.immotef.featureflag.FeatureFlag
import com.immotef.featureflag.FeatureFlagManager
import com.immotef.featureflag.FeatureViewManager
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */


internal class DashboardViewFeatureManager(
    private val featureFlagManager: FeatureFlagManager,
    private val coroutineUtils: CoroutineUtils) : FeatureViewManager {
    override fun handleView(view: View) {
        coroutineUtils.globalScope.launch {
            withContext(coroutineUtils.main) {
                val showShareButton = withContext(coroutineUtils.io) {
                    featureFlagManager.getFeatureFlag(FeatureFlag.DisplayShareButton)
                }
                view.dashboardShareButton.setVisibleOrGone(showShareButton)
            }
        }
    }
}