package com.immotef.featureflag

import com.immotef.core.CoroutineUtils
import com.immotef.featureflag.load.SaveFeatureFlagUseCase
import com.immotef.preferences.PreferencesFacade
import com.immotef.testutils.MainCoroutineScopeRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *
 */
@ExperimentalCoroutinesApi
class FeatureFlagManagerImpTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    lateinit var preferencesFacade: PreferencesFacade
    lateinit var coroutineUtils: CoroutineUtils
    lateinit var saveFeatureFlagUseCase: SaveFeatureFlagUseCase

    lateinit var featureFlagManager: FeatureFlagManager


    val testIoDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        preferencesFacade = mock()
        coroutineUtils = mock()
        saveFeatureFlagUseCase = mock()

        whenever(coroutineUtils.io).thenReturn(testIoDispatcher)
        whenever(coroutineUtils.globalScope).thenReturn(coroutineScope)
    }

    @Test
    fun ` when no previous key trigger facade at manager starts`() {
        runBlockingTest {
            //given
            whenever(preferencesFacade.contains(FeatureFlag.ShowOnboarding.name)).thenReturn(false)


            //when
            featureFlagManager = FeatureFlagManagerImp(preferencesFacade, coroutineUtils, saveFeatureFlagUseCase, listOf(FeatureFlag.ShowOnboarding))

            //then
            verify(saveFeatureFlagUseCase).saveFeatureFlag()

        }
    }

    @Test
    fun ` when key for feature flag exist do not trigger use case on start`() {
        runBlockingTest {
            //given
            whenever(preferencesFacade.contains(FeatureFlag.ShowOnboarding.name)).thenReturn(true)

            //when
            featureFlagManager = FeatureFlagManagerImp(preferencesFacade, coroutineUtils, saveFeatureFlagUseCase, listOf(FeatureFlag.ShowOnboarding))

            //then
            verifyZeroInteractions(saveFeatureFlagUseCase)
        }
    }

    @Test
    fun ` test that when one of need flag does not exist load flags`() {
        runBlockingTest {
            //given
            whenever(preferencesFacade.contains(FeatureFlag.ShowOnboarding.name)).thenReturn(true)
            whenever(preferencesFacade.contains(FeatureFlag.DisplayShareButton.name)).thenReturn(false)

            //when
            featureFlagManager = FeatureFlagManagerImp(
                preferencesFacade,
                coroutineUtils,
                saveFeatureFlagUseCase,
                listOf(FeatureFlag.ShowOnboarding, FeatureFlag.DisplayShareButton)
            )

            //then
            verify(saveFeatureFlagUseCase).saveFeatureFlag()
        }
    }
    @Test
    fun ` ask preferences manager for proper key when asking for feature flag`() {
        runBlockingTest {
            //given
            val featureFlag = FeatureFlag.DisplayShareButton
            whenever(preferencesFacade.contains(FeatureFlag.DisplayShareButton.name)).thenReturn(true)
            whenever(preferencesFacade.retrieveBoolean(featureFlag.name)).thenReturn(true)
            featureFlagManager = FeatureFlagManagerImp(preferencesFacade, coroutineUtils, saveFeatureFlagUseCase, listOf(featureFlag))

            //when then
            featureFlagManager.getFeatureFlag(featureFlag) shouldBe true
            verify(preferencesFacade).retrieveBoolean(featureFlag.name)
        }
    }
}