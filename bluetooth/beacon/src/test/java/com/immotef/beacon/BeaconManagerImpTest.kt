package com.immotef.beacon

import com.immotef.preferences.PreferencesFacade
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Test

/**
 *
 */
class BeaconManagerImpTest {


    private lateinit var facade: PreferencesFacade

    private lateinit var manager: BeaconManager


    @Before
    fun setup() {
        facade = mock()
        manager = BeaconManagerImp(facade)
    }

    @Test
    fun `test provide major minor hex`() {
        //given
        val major = 123 //7b
        val minor = 2761 //"ac9"

        manager.setMajorMinor(major, minor)

        //when then
        manager.provideMajorMinorHex() shouldBe "007b0ac9"
    }

    @Test
    fun `test provide major minor from hex`() {
        //given
        val major = 123 //7b
        val minor = 2761 //"ac9"


        //when then
        manager.provideMajorMinorFromHex("007b0ac9") shouldBe MajorMinor(major, minor)
    }
}