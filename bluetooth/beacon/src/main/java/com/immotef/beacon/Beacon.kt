package com.immotef.beacon

import kotlin.math.pow


/**
 *
 */


data class Beacon(val uuid: String,
                  val major: Int,
                  val minor: Int,
                  val timestamp: Long,
                  val rssi: Int = 0,
                  val txPower: Int = 0) {


    fun calculateDistance(): Double {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        var ratio: Double = rssi.toDouble() / txPower.toDouble()
        val a: Double = 0.89976
        return if (ratio < 1.0) {
            ratio.pow(10)
        } else {
            a * (ratio.pow(7.7095)) + 0.111
        }
    }

    fun isClose(): Boolean = rssi >= -70
}