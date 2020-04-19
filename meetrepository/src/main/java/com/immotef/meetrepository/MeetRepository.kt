package com.immotef.meetrepository

import com.immotef.beacon.Beacon
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import kotlin.math.ceil

/**
 *
 */


interface MeetRepository {
    suspend fun addBeaconEvent(event: Beacon)
}

internal interface BeaconCloseChecker {
    fun checkIsBeaconClose(event: Beacon): Boolean = event.isClose()

    fun createMeet(meet: Meet, event: Beacon): Meet {
        val calculatedDistance = calculateDistance(meet, event)
        val calculatedTime = event.timestamp - meet.endTime + meet.closeDistanceTime
        return meet.copy(
            endTime = event.timestamp, uploadedToServer = false, closeDistance = calculatedDistance, closeDistanceTime = calculatedTime
        )
    }

    private fun calculateDistance(meet: Meet, event: Beacon) = if (meet.closeDistance == -1) {
        calculateDistance(event)
    } else {
        kotlin.math.min(meet.closeDistance.toDouble(), calculateDistance(event).toDouble()).toInt()
    }

    fun calculateDistance(event: Beacon) = if (checkIsBeaconClose(event)) ceil(event.calculateDistance()).toInt() else -1
}


internal class MeetRepositoryImp(
    private val dao: MeetDAO,
    private val threshold: Long = 1000 * 30,
    private val beaconCloseChecker: BeaconCloseChecker) : MeetRepository {

    override suspend fun addBeaconEvent(event: Beacon) {
        val meet = dao.getMeetWithSpecificMajorMinor(event.major, event.minor)
        if (meet != null && event.timestamp - meet.endTime < threshold) {
            val meetToUpdate = if (beaconCloseChecker.checkIsBeaconClose(event)) {
                beaconCloseChecker.createMeet(meet, event)
            } else {
                meet.copy(endTime = event.timestamp, uploadedToServer = false)
            }
            dao.updateMeet(meetToUpdate)
        } else {
            val secondMeet = Meet(
                major = event.major,
                minor = event.minor,
                startTime = event.timestamp,
                endTime = event.timestamp,
                closeDistance = beaconCloseChecker.calculateDistance(event)
            )
            dao.insertMeet(secondMeet)
        }
    }

}