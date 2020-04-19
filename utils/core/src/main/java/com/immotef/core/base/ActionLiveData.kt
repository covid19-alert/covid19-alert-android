package com.immotef.core.base

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class ActionLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasObservers()) {
            throw Throwable("Only one observer at a time may subscribe to a ActionLiveData")
        }
        super.observe(owner, Observer { data ->
            // We ignore any null values and early return
            if (data != null){
                observer.onChanged(data)
                value = null
            }
        })
    }


    // Just a nicely named method that wraps setting the value
    @MainThread
    fun sendAction(data: T) {
        value = data
    }
}