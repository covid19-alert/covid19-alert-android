package com.immotef.core.delegate

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import kotlin.reflect.KProperty

/**
 *
 */



class BoundCoroutineScopeDelegate {
    operator fun getValue(thisRef: Any, property: KProperty<*>): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

}