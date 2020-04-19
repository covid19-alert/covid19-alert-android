package com.immotef.core.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

inline fun <reified T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T) -> Unit) = observe(owner, Observer { observer(it) })

inline fun <reified T> Fragment.observe(liveData: LiveData<T>, crossinline observer: (T) -> Unit) = liveData.observe(viewLifecycleOwner, Observer { observer(it) })
inline fun <reified T> AppCompatActivity.observe(liveData: LiveData<T>, crossinline observer: (T) -> Unit) = liveData.observe(this, Observer { observer(it) })



fun <X, Y> LiveData<X>.map(mapFunction: (X) -> (Y)): LiveData<Y> = Transformations.map(this, mapFunction)
