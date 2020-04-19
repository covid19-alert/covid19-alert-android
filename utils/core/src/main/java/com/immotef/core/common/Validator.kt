package com.immotef.core.common

/**
 *
 */


interface Validator<T> {
    fun validate(t: T): Boolean
}