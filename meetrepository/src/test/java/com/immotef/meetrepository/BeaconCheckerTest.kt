package com.immotef.meetrepository

import com.immotef.beacon.Beacon
import com.immotef.db.meet.Meet
import io.kotlintest.shouldBe
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil

/**
 *
 */


class BeaconCheckerTest {
    private lateinit var beaconCloseChecker: BeaconCloseChecker

    val beacon = Beacon("uuid,",1,2,1,-70,0)
    val meet = Meet(major = 1,minor = 2,startTime = 100,endTime = 100,closeDistance = -1)

    @Before
    fun setUp() {
        beaconCloseChecker = object :BeaconCloseChecker{}
    }

    @Test
    fun `is close when rssi is larger than -70`(){
        //given
        val beacon = beacon.copy(rssi = -50)

        //when then
        beaconCloseChecker.checkIsBeaconClose(beacon) shouldBe true
    }

    @Test
    fun `is close when rssi is equal -70`(){
        //given
        val beacon  = beacon.copy(rssi = -70)

        //when then
        beaconCloseChecker.checkIsBeaconClose(beacon) shouldBe true
    }

    @Test
    fun `is lower then -70 then is not close `(){
        //given
        val beacon  = beacon.copy(rssi = -100)

        //when then
        beaconCloseChecker.checkIsBeaconClose(beacon) shouldBe false
    }


    @Test
    fun `calculate distance when is not close `(){
        //given
        val beacon  = beacon.copy(rssi = -79)

        //when then
        beaconCloseChecker.calculateDistance(beacon) shouldBe -1
    }

    @Test
    fun `calculate distance when is  close `(){
        //given
        val beacon = beacon.copy(rssi = -30, txPower = -59)

        //when then
        beaconCloseChecker.calculateDistance(beacon) shouldBe ceil(beacon.calculateDistance()).toInt()
    }

    @Test
    fun `Create meet when calculated distance is -1`(){
        //given
        val beacon  = beacon.copy(rssi = -70,txPower = -59,timestamp = 110)
        val testMeet = meet.copy(closeDistance = -1,endTime = 100,closeDistanceTime = 0)
        //when then
        beaconCloseChecker.createMeet(testMeet,beacon) shouldBe testMeet.copy(endTime = beacon.timestamp,closeDistance = ceil(beacon.calculateDistance()).toInt(),closeDistanceTime = beacon.timestamp-testMeet.endTime)
    }

    @Test
    fun `Create meet when calculated distance is lower than calculated`(){
        //given
        val beacon  = beacon.copy(rssi = -70,txPower = -59,timestamp = 110)
        val testMeet = meet.copy(closeDistance = beacon.calculateDistance().toInt()-1,endTime = 100,closeDistanceTime = 182)
        //when then
        val closeDistanceTimeThatShouldBe = beacon.timestamp - testMeet.endTime + testMeet.closeDistanceTime
        beaconCloseChecker.createMeet(testMeet,beacon) shouldBe testMeet.copy(endTime = beacon.timestamp,closeDistance = testMeet.closeDistance,closeDistanceTime = closeDistanceTimeThatShouldBe)
    }

}