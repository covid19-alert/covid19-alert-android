package com.immotef.onboarding.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.immotef.authorization.AuthorizationSaver
import com.immotef.onboarding.OnBoardingStateTrigger
import kotlinx.coroutines.launch

internal class OnBoardingViewModel(
    private val authorizationSaver: AuthorizationSaver,
    private val state: OnBoardingStateTrigger)
    : ViewModel() {

    fun finishOnBoarding() {
        viewModelScope.launch {
            authorizationSaver.saveOnBoardingFinished()
            state.finishOnBoarding()
        }
    }
}