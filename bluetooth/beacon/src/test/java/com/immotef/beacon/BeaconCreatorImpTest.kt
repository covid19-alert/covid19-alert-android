package com.immotef.beacon

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.shouldBe
import org.junit.Test

/**
 *
 */
class BeaconCreatorImpTest {


    private val beaconSetupProvider: BeaconSetupProvider = mock()

    @Test
    fun `test how does it work`() {
        //given
        val uuid = "20354d7a-e4fe-47af-abcd-ef0123456789"
        val major = 2716
        val minor = 1
        whenever(beaconSetupProvider.provideMajorHex()).thenReturn(Integer.toHexString(major).padStart(4, '0'))
        whenever(beaconSetupProvider.provideMinorHex()).thenReturn(Integer.toHexString(minor).padStart(4, '0'))


        val beaconCreator = BeaconCreatorImp(uuid, beaconSetupProvider)

        //when then
        val majMin = beaconCreator.provideMajorMinor()


        majMin.major shouldBe "60F2".toInt(16)
        majMin.minor shouldBe "6667".toInt(16)

    }


    @Test
    fun `test how does it work 2`() {
        //given
        val uuid = "20354d7a-e4fe-47af-2b9d-ef01a745638c"
        val major = 271
        val minor = 1276
        whenever(beaconSetupProvider.provideMajorHex()).thenReturn(Integer.toHexString(major).padStart(4, '0'))
        whenever(beaconSetupProvider.provideMinorHex()).thenReturn(Integer.toHexString(minor).padStart(4, '0'))


        val beaconCreator = BeaconCreatorImp(uuid, beaconSetupProvider)

        //when then
        val majMin = beaconCreator.provideMajorMinor()


        majMin.major shouldBe "6765".toInt(16)
        majMin.minor shouldBe "6A5F".toInt(16)

    }
}