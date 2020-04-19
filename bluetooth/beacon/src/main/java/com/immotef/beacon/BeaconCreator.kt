package com.immotef.beacon

/**
 *
 */


interface BeaconCreator {
    fun provideUUID(): String
    fun provideMajorMinor(): MajorMinor
    fun provideRealMajorMinorFromMajorMinorBeacon(majorMinor: MajorMinor): MajorMinor
}


class BeaconCreatorImp(val uuid: String,
                       private val beaconSetupProvider: BeaconSetupProvider) : BeaconCreator {

    private val lastPartOfUUID = uuid.replace("-", "").substring(16)

    init {
        if (lastPartOfUUID.toCharArray().distinct().size != 16) {
            throw IllegalArgumentException("WRong UUID")
        }
    }

    override fun provideUUID(): String = uuid

    override fun provideMajorMinor(): MajorMinor {
        val minor = beaconSetupProvider.provideMinorHex()
        val major = beaconSetupProvider.provideMajorHex()
        val majorToSend = major.fold("", { acc, c -> "$acc${Integer.toHexString(lastPartOfUUID.indexOf(c))}" })
        val minorToSend = minor.fold("", { acc, c -> "$acc${Integer.toHexString(lastPartOfUUID.indexOf(c))}" })

        return MajorMinor(majorToSend.toInt(16), minorToSend.toInt(16))
    }

    override fun provideRealMajorMinorFromMajorMinorBeacon(majorMinor: MajorMinor): MajorMinor {
        TODO("Not yet implemented")
    }
}