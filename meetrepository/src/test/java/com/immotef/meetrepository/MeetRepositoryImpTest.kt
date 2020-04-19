package com.immotef.meetrepository

import com.immotef.beacon.Beacon
import com.immotef.dashboardrepository.DashboardRepository
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import com.immotef.testutils.MainCoroutineScopeRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


/**
 *
 */
internal class MeetRepositoryImpTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    lateinit var dao: MeetDAO
    private val treshold = 10L
    lateinit var beaconCloseChecker: BeaconCloseChecker
    lateinit var repository: MeetRepository
    lateinit var dashboardRepository: DashboardRepository


    @Before
    fun setUp() {
        dao = mock()
        beaconCloseChecker = mock()
        dashboardRepository = mock()
        repository = MeetRepositoryImp(dao, treshold, beaconCloseChecker)
    }

    @Test
    fun `when there is no meet in db insert new one`() {
        runBlockingTest {
            //given
            val event = Beacon("uuid", 1, 2, 123L)
            val returnedDistance = 18272
            whenever(beaconCloseChecker.calculateDistance(event)).thenReturn(returnedDistance)
            whenever(dao.getMeetWithSpecificMajorMinor(event.major, event.minor)).thenReturn(null)

            //when
            repository.addBeaconEvent(event)

            //then
            verify(dao).insertMeet(
                Meet(
                    major = event.major,
                    minor = event.minor,
                    startTime = event.timestamp,
                    endTime = event.timestamp,
                    closeDistance = returnedDistance
                )
            )
        }
    }


    @Test
    fun `when there is meet with treshold condition true update existing one in db`() {
        runBlockingTest {
            //given
            val event = Beacon("uuid", 1, 2, 1000)
            val meet = Meet(event.major, event.minor, 1, event.timestamp - treshold + 1, false, meetId = 5)
            whenever(beaconCloseChecker.checkIsBeaconClose(event)).thenReturn(false)
            whenever(dao.getMeetWithSpecificMajorMinor(event.major, event.minor)).thenReturn(meet)

            //when
            repository.addBeaconEvent(event)

            //then
            val meetThatShouldBeInserted = meet.copy(endTime = event.timestamp)
            verify(dao).updateMeet(meetThatShouldBeInserted)
        }
    }

    @Test
    fun `when there is meet with treshold condition true and in close range update existing one in db with proper values`() {
        runBlockingTest {
            //given
            val event = Beacon("uuid", 1, 2, 1000)
            val meet = Meet(event.major, event.minor, 1, event.timestamp - treshold + 5, false, meetId = 5, closeDistanceTime = 123)
            whenever(beaconCloseChecker.checkIsBeaconClose(event)).thenReturn(true)
            whenever(dao.getMeetWithSpecificMajorMinor(event.major, event.minor)).thenReturn(meet)
            val someTottalyDifferentMeet =
                Meet(event.major, event.minor, 123412, event.timestamp - treshold + 5, false, meetId = 123241, closeDistanceTime = 123)
            whenever(beaconCloseChecker.createMeet(meet, event)).thenReturn(someTottalyDifferentMeet)

            //when
            repository.addBeaconEvent(event)

            //then
            verify(dao).updateMeet(someTottalyDifferentMeet)
        }
    }

    @Test
    fun `when there is meet with treshold condition false create new meet in db`() {
        runBlockingTest {
            //given
            val event = Beacon("uuid", 1, 2, 1000)
            val meet = Meet(event.major, event.minor, 1, event.timestamp - treshold - 1, false, meetId = 5)
            val returnedDistance = 18272
            whenever(beaconCloseChecker.calculateDistance(event)).thenReturn(returnedDistance)
            whenever(dao.getMeetWithSpecificMajorMinor(event.major, event.minor)).thenReturn(meet)

            //when
            repository.addBeaconEvent(event)

            //then
            verify(dao).insertMeet(
                Meet(
                    major = event.major,
                    minor = event.minor,
                    startTime = event.timestamp,
                    endTime = event.timestamp,
                    closeDistance = returnedDistance
                )
            )
        }
    }

    @Test
    fun `when there is meet with treshold condition true update existing one in db and change uploaded to server `() {
        runBlockingTest {
            //given
            val event = Beacon("uuid", 1, 2, 1000)
            whenever(beaconCloseChecker.checkIsBeaconClose(event)).thenReturn(false)
            val meet = Meet(event.major, event.minor, 1, event.timestamp - treshold + 1, true, meetId = 5)
            whenever(dao.getMeetWithSpecificMajorMinor(event.major, event.minor)).thenReturn(meet)

            //when
            repository.addBeaconEvent(event)

            //then
            val meetThatShouldBeInserted = meet.copy(endTime = event.timestamp, uploadedToServer = false)
            verify(dao).updateMeet(meetThatShouldBeInserted)
        }
    }

}