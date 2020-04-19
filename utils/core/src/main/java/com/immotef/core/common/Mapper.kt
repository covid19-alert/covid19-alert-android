package com.immotef.core.common

/**
 *
 */


interface Mapper<in F, out T> {
    fun map(from: F): T
}