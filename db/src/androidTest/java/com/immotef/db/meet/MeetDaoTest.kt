package com.immotef.db.meet

import android.content.Context
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.immotef.db.CoronaVirusDatabase
import com.immotef.testutils.MainCoroutineScopeRule
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch

/**
 *
 */


@RunWith(AndroidJUnit4ClassRunner::class)
class MeetDaoTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()


    private lateinit var meetDAO: MeetDAO
    private lateinit var db: CoronaVirusDatabase

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context

        db = Room.inMemoryDatabaseBuilder(
            context, CoronaVirusDatabase::class.java
        ).build()
        meetDAO = db.getMeetDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertMeet() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)

            //when
            meetDAO.insertMeet(insertedMeet)
            val meetsFromDb = meetDAO.getMeets()

            //then
            meetsFromDb shouldHaveSize 1
            with(meetsFromDb[0]) {
                major shouldBe insertedMeet.major
                minor shouldBe insertedMeet.minor
                startTime shouldBe insertedMeet.startTime
                endTime shouldBe insertedMeet.endTime
                meetId shouldNotBe insertedMeet.meetId
            }
        }
    }

    @Test
    fun testUpdate() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)

            //when
            meetDAO.insertMeet(insertedMeet)
            val meetFromDb = meetDAO.getMeets()[0]
            val someTottalyDifferentTime: Long = 12345
            val updatedMeet = meetFromDb.copy(endTime = someTottalyDifferentTime)
            meetDAO.updateMeet(updatedMeet)

            val number = meetDAO.getMeets()
            val meetFromDbAfterUpdate = number[0]
            //then
            meetFromDbAfterUpdate.endTime shouldBe someTottalyDifferentTime
            meetFromDbAfterUpdate.meetId shouldBe meetFromDb.meetId

        }
    }

    @Test
    fun testGetLastWithSpecific() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)
            val insertedMeet2 = Meet(1, 2, 5, 6)
            val insertedMeet3 = Meet(1, 2, 9, 10)

            //when
            meetDAO.insertMeet(insertedMeet)
            meetDAO.insertMeet(insertedMeet2)
            meetDAO.insertMeet(insertedMeet3)

            val meetFromDb = meetDAO.getMeetWithSpecificMajorMinor(insertedMeet.major, insertedMeet.minor)

            //then
            meetFromDb shouldNotBe null
            checkMeetFromDbWithInsertedOne(meetFromDb, insertedMeet3)

        }
    }

    private fun checkMeetFromDbWithInsertedOne(meetFromDb: Meet?, insertedMeet3: Meet) {
        meetFromDb!!.major shouldBe insertedMeet3.major
        meetFromDb.minor shouldBe insertedMeet3.minor
        meetFromDb.endTime shouldBe insertedMeet3.endTime
        meetFromDb.startTime shouldBe insertedMeet3.startTime
    }

    @Test
    fun testWhenFirstTimeGetWithSpecificMinorMajorReturnsNull() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)
            val insertedMeet2 = Meet(1, 2, 5, 6)
            val insertedMeet3 = Meet(1, 2, 9, 10)

            //when
            meetDAO.insertMeet(insertedMeet)
            meetDAO.insertMeet(insertedMeet2)
            meetDAO.insertMeet(insertedMeet3)

            val meetFromDb = meetDAO.getMeetWithSpecificMajorMinor(123, 212)

            //then
            meetFromDb shouldBe null
        }
    }

    @Test
    fun testGetAppNotUploadedToServer() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)
            val insertedMeet2 = Meet(1, 2, 5, 6, uploadedToServer = true)
            val insertedMeet3 = Meet(1, 2, 9, 10)
            val insertedMeet4 = Meet(1, 4, 9, 22, uploadedToServer = true)

            //when
            meetDAO.insertMeet(insertedMeet)
            meetDAO.insertMeet(insertedMeet2)
            meetDAO.insertMeet(insertedMeet3)
            meetDAO.insertMeet(insertedMeet4)

            val meets = meetDAO.getAllMeetsWithSpecificUploadedToServer()
            val meetsUploadedToServer = meetDAO.getAllMeetsWithSpecificUploadedToServer(uploadedToServer = true)

            //then
            meets shouldHaveSize 2
            meetsUploadedToServer shouldHaveSize 2
            checkMeetFromDbWithInsertedOne(meets[0], insertedMeet)
            checkMeetFromDbWithInsertedOne(meets[1], insertedMeet3)
            checkMeetFromDbWithInsertedOne(meetsUploadedToServer[0], insertedMeet2)
            checkMeetFromDbWithInsertedOne(meetsUploadedToServer[1], insertedMeet4)

        }
    }

    @Test
    fun testFlowGet() {
        runBlocking {
            //given
            val insertedMeet = Meet(1, 2, 1, 1)
            val tottalyDifferentMeet = Meet(2, 3, 4, 1)
            var loadedValues = mutableListOf<List<Meet>>()
            meetDAO.insertMeet(insertedMeet)
            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)

            val job = async(Dispatchers.IO) {
                meetDAO.getFlowMeets().collect {

                    when (loadedValues.size) {
                        0 -> {
                            loadedValues.add(it)
                            firstResultLatch.countDown()
                        }
                        1 -> {
                            loadedValues.add(it)
                            secondResultLatch.countDown()
                        }
                        else -> fail("Should have only collected 2 results.")

                    }
                }
            }
            //when

            firstResultLatch.await()
            //then
            loadedValues shouldHaveSize 1
            loadedValues[0] shouldHaveSize 1
            checkMeetFromDbWithInsertedOne(loadedValues[0][0], insertedMeet)

            //when
            meetDAO.insertMeet(tottalyDifferentMeet)
            secondResultLatch.await()
            //then
            loadedValues shouldHaveSize 2
            loadedValues[1] shouldHaveSize 2
            checkMeetFromDbWithInsertedOne(loadedValues[1][0], insertedMeet)
            checkMeetFromDbWithInsertedOne(loadedValues[1][1], tottalyDifferentMeet)

            job.cancelAndJoin()
        }

    }
}

