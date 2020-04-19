package com.immotef.core.errors

import androidx.fragment.app.Fragment

/**
 *
 */


data class ErrorWrapper(val text: String,
                        val reason: String,
                        val action: ((Fragment) -> Unit)? = null,
                        val errorCode: Int = -1)