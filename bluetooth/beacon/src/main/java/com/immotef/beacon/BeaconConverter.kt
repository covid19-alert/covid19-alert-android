package com.immotef.beacon

/**
 *
 */


interface BeaconConverter {
    fun provideMajorMinorHex(): String
    fun provideMajorMinorFromHex(hex: String): MajorMinor?
}


