package com.immotef.reportinfection.mvvm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.immotef.core.CoroutineUtils
import com.immotef.core.base.BaseViewModel

/**
 *
 */





internal class FlowViewModel( coroutineUtils: CoroutineUtils):BaseViewModel(coroutineUtils){
    var uri:Uri? = null

    private val goBackProcessor: MutableLiveData<Unit> = MutableLiveData()
    val goBackStream: LiveData<Unit> get() = goBackProcessor


    fun navigateBack(){
        goBackProcessor.postValue(Unit)
    }
}