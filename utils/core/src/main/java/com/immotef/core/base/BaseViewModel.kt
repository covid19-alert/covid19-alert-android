package com.immotef.core.base

import androidx.lifecycle.*
import com.immotef.core.CoroutineUtils
import com.immotef.core.errors.ErrorWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *
 */


abstract class BaseViewModel(protected val coroutineUtils: CoroutineUtils):ViewModel() {


    protected val errorHandler = CoroutineExceptionHandler { _, throwable ->
        errorProcessor.postValue(coroutineUtils.produce(throwable))
        showProgressProcessor.postValue(false)
    }

    protected val errorProcessor: MutableLiveData<ErrorWrapper> = MutableLiveData()
    val errorWrapperStream: LiveData<ErrorWrapper> get() = errorProcessor

    protected val showProgressProcessor: MutableLiveData<Boolean> = MutableLiveData(false)

    val showProgressStream: LiveData<Boolean>
        get() = showProgressProcessor.distinctUntilChanged()

    fun launchWithProgress(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(errorHandler) {
            showProgressProcessor.postValue(true)
            block()
            showProgressProcessor.postValue(false)
        }
    }

}

