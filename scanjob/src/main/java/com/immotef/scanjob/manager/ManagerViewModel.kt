package com.immotef.scanjob.manager

import com.immotef.core.CoroutineUtils
import com.immotef.core.delegate.BoundCoroutineScopeDelegate
import com.immotef.featureflag.FeatureFlagManager
import com.immotef.meetrepository.MeetRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *
 */


internal class ManagerViewModel(
    private val featureFlag: FeatureFlagManager,
    private val coroutineUtils: CoroutineUtils,
    private val meetRepository: MeetRepository
) {
    private val scope by BoundCoroutineScopeDelegate()

    fun start() {
        scope.launch {

        }
    }



    fun stop() {
        scope.launch {

        }
    }

    fun uploadData() {
        scope.launch {

        }
    }

    fun onDestroy() {
        scope.cancel()
    }

}