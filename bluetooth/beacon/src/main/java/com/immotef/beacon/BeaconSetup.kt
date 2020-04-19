package com.immotef.beacon

import com.immotef.preferences.PreferencesFacade
import kotlinx.coroutines.runBlocking

/**
 *
 */


interface BeaconSetup {
    fun setMajorMinor(major: Int, minor: Int)
}

data class MajorMinor(val major: Int, val minor: Int)
interface BeaconSetupProvider {
    fun provideMinor(): Int
    fun provideMajor(): Int
    fun provideMinorHex(): String
    fun provideMajorHex(): String
    fun provideUUID(): String
    fun provideDroppedUUID(): String
    fun compareDroppedUUID(uuid: String, shouldCheckAreNotExactlyTheSame: Boolean = false): Boolean
    fun takeMajorMinorFromUUID(uuid: String): MajorMinor
    fun provideLayout(): String
}


private const val UUID_CONST = "43DB3082-A889-4510-902A-E99E5EDB9504"
private const val UUID_CONST2 = "43DB3082-A889-4510-902A-434F56313921"

private const val BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
private const val MAJOR_KEY = "major_key_tralala"
private const val MINOR_KEY = "minor_key_tralala"

internal interface BeaconManager : BeaconSetupProvider, BeaconSetup, BeaconConverter


internal class BeaconManagerImp(private val facade: PreferencesFacade,
                                private val UUID: String = UUID_CONST,
                                private val beaconLayout: String = BEACON_LAYOUT) : BeaconManager {
    private var major: Int = -1
    private var minor: Int = -1
    override fun setMajorMinor(major: Int, minor: Int) {
        runBlocking {
            this@BeaconManagerImp.major = major
            this@BeaconManagerImp.minor = minor
            facade.saveInt(major, MAJOR_KEY)
            facade.saveInt(minor, MINOR_KEY)
        }
    }

    override fun provideMajorMinorHex(): String =
        "${Integer.toHexString(provideMajor()).padStart(4, '0')}${Integer.toHexString(provideMinor()).padStart(4, '0')}"

    override fun provideMinorHex(): String = Integer.toHexString(provideMinor()).padStart(4, '0')
    override fun provideMajorHex(): String = Integer.toHexString(provideMajor()).padStart(4, '0')

    override fun provideMajorMinorFromHex(hex: String): MajorMinor? =
        try {
            MajorMinor(Integer.parseInt(hex.substring(0, 4), 16), Integer.parseInt(hex.substring(4), 16))
        } catch (e: Exception) {
            null
        }

    override fun provideMinor(): Int {
        if (minor == -1) {
            minor = runBlocking {
                val value = facade.retrieveInt(MINOR_KEY)
                if (value == -1000) {
                    facade.saveInt(2, MINOR_KEY)
                    return@runBlocking 2
                }
                value
            }
        }
        return minor
    }

    override fun provideMajor(): Int {
        if (major == -1) {
            major = runBlocking {
                val value = facade.retrieveInt(MAJOR_KEY)
                if (value == -1000) {
                    facade.saveInt(1, MAJOR_KEY)
                    return@runBlocking 1
                }
                value
            }
        }
        return major
    }

    override fun provideUUID(): String = UUID
    override fun provideDroppedUUID(): String =
        UUID.dropLast(8).let { droppedUDID -> "$droppedUDID${provideMajor().toString(16).padStart(4, '0')}${provideMinor().toString(16).padStart(4, '0')}" }

    override fun compareDroppedUUID(uuid: String, shouldCheckAreNotExactlyTheSame: Boolean): Boolean =
        uuid.dropLast(8).toLowerCase() == UUID.dropLast(8).toLowerCase() && (if (shouldCheckAreNotExactlyTheSame) uuid != UUID.toLowerCase() else true)

    override fun takeMajorMinorFromUUID(uuid: String): MajorMinor = uuid.drop(uuid.length - 8).let {
        MajorMinor(it.substring(0, 4).toInt(16), it.substring(4).toInt(16))
    }

    override fun provideLayout(): String = beaconLayout
}