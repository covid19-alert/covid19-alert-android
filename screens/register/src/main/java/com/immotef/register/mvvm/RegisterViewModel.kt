package com.immotef.register.mvvm

import androidx.lifecycle.viewModelScope
import com.immotef.authorization.AuthorizationProvider
import com.immotef.authorization.LoggedState
import com.immotef.authorization.NeedOnBoarding
import com.immotef.core.CoroutineUtils
import com.immotef.core.base.BaseViewModel
import com.immotef.dynamic_links.DeepLinkStateTrigger
import com.immotef.register.RegisterStateTrigger
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


internal class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val registerStateTrigger: RegisterStateTrigger,
    private val authorizationProvider: AuthorizationProvider,
    private val deepLinkStateTrigger: DeepLinkStateTrigger,
    coroutineUtils: CoroutineUtils
) : BaseViewModel(coroutineUtils) {


    init {
        viewModelScope.launch {
            when (authorizationProvider.isLogged()) {
                NeedOnBoarding -> registerStateTrigger.openOnboarding()
                LoggedState -> registerStateTrigger.finishRegisterState()
                else -> {
                }
            }
        }
    }

    fun register() {
        showProgressProcessor.postValue(true)
        viewModelScope.launch(errorHandler) {
            withContext(coroutineUtils.io) {
                registerUseCase.register()
            }
            if(!deepLinkStateTrigger.isInfectionStateOn()){
                registerStateTrigger.finishRegisterState()
            }else{
                registerStateTrigger.finishRegisterStateWithInfectionState()
            }
            showProgressProcessor.postValue(false)
        }
    }
}