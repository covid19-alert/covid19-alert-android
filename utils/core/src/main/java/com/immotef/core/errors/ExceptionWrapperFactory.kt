package com.immotef.core.errors

/**
 *
 */


interface ExceptionWrapperFactory {
    fun produce(throwable: Throwable?): ErrorWrapper
}