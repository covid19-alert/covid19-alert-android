package com.immotef.db.infected

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.immotef.db.CoronaVirusDatabase
import io.kotlintest.fail
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch

/**
 *
 */


class InfectedDaoTest {
    private lateinit var dao: InfectedDAO
    private lateinit var db: CoronaVirusDatabase

    @Before
    fun createDb() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context

        db = Room.inMemoryDatabaseBuilder(
            context, CoronaVirusDatabase::class.java
        ).build()
        dao = db.getInfectedDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    private val infected = Infected("id")

    @Test
    fun testInsertInfected() {
        runBlocking {
            //when
            dao.updateInfected(listOf(infected))

            //then
            dao.getInfected().apply {
                this shouldHaveSize 1
                get(0).id shouldBe infected.id

            }
        }
    }

    @Test
    fun testFLowInfected() {
        runBlocking {
            //given
            val insertedInfected = Infected("id1")
            val tottalyDifferentInfected = Infected("id2")
            val loadedValues = mutableListOf<List<Infected>>()
            dao.updateInfected(listOf(insertedInfected))
            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)

            val job = async(Dispatchers.IO) {
                dao.getInfectedFlow().collect {

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
            loadedValues[0][0].id shouldBe insertedInfected.id

            //when
            dao.updateInfected(listOf(tottalyDifferentInfected))
            secondResultLatch.await()
            //then
            loadedValues shouldHaveSize 2
            loadedValues[1] shouldHaveSize 2
            loadedValues[1][0].id shouldBe insertedInfected.id
            loadedValues[1][1].id shouldBe tottalyDifferentInfected.id

            job.cancelAndJoin()
        }
    }

    @Test
    fun testWhenNothingInDbShouldReturnEmptyListInFlow() {
        runBlocking {
            //given

            val loadedValues = mutableListOf<List<Infected>>()

            val firstResultLatch = CountDownLatch(1)
            val secondResultLatch = CountDownLatch(1)

            val job = async(Dispatchers.IO) {
                dao.getInfectedFlow().collect {

                    when (loadedValues.size) {
                        0 -> {
                            loadedValues.add(it)
                            firstResultLatch.countDown()
                        }
                        else -> fail("Should have only collected 2 results.")

                    }
                }
            }
            //when

            firstResultLatch.await()
            //then
            loadedValues shouldHaveSize 1
            loadedValues[0] shouldHaveSize 0


            job.cancelAndJoin()
        }
    }
}