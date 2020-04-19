package com.immotef.core.info

import android.os.Build

/**
 *
 */


interface DeviceInfoProvider {
    fun provideDeviceInfo(): String
}

internal class DeviceInfoProviderImp : DeviceInfoProvider {
    override fun provideDeviceInfo(): String = "Device-info:" +
            "\nOS Version: ${System.getProperty("os.version")}(${Build.VERSION.INCREMENTAL})" +
            "\nOS API Level: ${Build.VERSION.SDK_INT}" +
            "\nDevice: ${Build.DEVICE}" +
            "\nModel (and Product): ${Build.MODEL} (${Build.PRODUCT})"

}