package com.immotef.infectedrepository

import com.immotef.db.infected.Infected
import com.immotef.db.infected.InfectedDAO
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import com.immotef.testutils.MainCoroutineScopeRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Collections.max

/**
 *
 */
class InfectedRepositoryImpTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    lateinit var meetDao: MeetDAO
    lateinit var infectedDAO: InfectedDAO

    lateinit var infectedRepository: InfectedRepository


    @Before
    fun setUp() {
        meetDao = mock()
        infectedDAO = mock()
        infectedRepository = InfectedRepositoryImp(infectedDAO, meetDao)
    }

    @Test
    fun `saving ids triggers proper dao method`() {
        runBlocking {
            //given
            val someFancyIds = listOf("id1", "id2", "id3")

            //when
            infectedRepository.saveInfected(someFancyIds)

            //then
            verify(infectedDAO).updateInfected(someFancyIds.map { Infected(it) })
        }
    }

    @Test
    fun `saving empty list does not triggers  dao `() {
        runBlocking {
            //given
            val someFancyIds = emptyList<String>()

            //when
            infectedRepository.saveInfected(someFancyIds)

            //then
            verifyZeroInteractions(infectedDAO)
        }
    }

    @Test
    fun `saving null list does not triggers dao `() {
        runBlocking {
            //given
            val someFancyIds = null

            //when
            infectedRepository.saveInfected(someFancyIds)

            //then
            verifyZeroInteractions(infectedDAO)
        }
    }

    @Test
    fun `get infected people simplest case`() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val infected = listOf(Infected(id))
            val meets = listOf(Meet(100, 1, 1, 100, false, 100, 100))

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect {
                trigger = true
                it shouldBe meets.map { InfectedMeet(it.userId(), it.startTime, it.endTime, it.closeDistance, it.closeDistanceTime) }
            }

            trigger shouldBe true
        }
    }

    @Test
    fun `get infected people should pass last endTime`() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val infected = listOf(Infected(id))
            val element = Meet(100, 1, 1, 100, false, closeDistanceTime = 10, closeDistance = 5)
            val element2 = element.copy(endTime = 122)
            val element3 = element.copy(endTime = 123)
            val meets = listOf(element, element2, element3)

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect {
                trigger = true
                it shouldHaveSize 1
                it[0].endTime shouldBe max(listOf(element.endTime, element2.endTime, element3.endTime))
            }

            trigger shouldBe true
        }
    }

    @Test
    fun `get infected people simplest combain value of  meets with  sum time of close distance`() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val infected = listOf(Infected(id))
            val element = Meet(100, 1, 1, 100, false, closeDistanceTime = 10, closeDistance = 5)
            val element2 = element.copy(closeDistanceTime = 12)
            val element3 = element.copy(closeDistanceTime = 3)
            val meets = listOf(element, element2, element3)

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect {
                trigger = true
                it shouldHaveSize 1
                it[0].closeDistanceTime shouldBe element.closeDistanceTime + element2.closeDistanceTime + element3.closeDistanceTime
            }

            trigger shouldBe true
        }
    }

    @Test
    fun `get infected people simplest combain value of  meets with  lowest of close distance`() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val infected = listOf(Infected(id))
            val element = Meet(100, 1, 1, 100, false, closeDistanceTime = 10, closeDistance = 5)
            val element2 = element.copy(closeDistance = 12)
            val element3 = element.copy(closeDistance = 3)
            val meets = listOf(element, element2, element3)

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect {
                trigger = true
                it shouldHaveSize 1
                it[0].closeDistance shouldBe listOf(element.closeDistance, element2.closeDistance, element3.closeDistance).min()
            }

            trigger shouldBe true
        }
    }

    @Test
    fun `do not involve close distance -1 `() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val infected = listOf(Infected(id))
            infected.reduce { acc, infected ->
                acc
            }
            val element = Meet(100, 1, 1, 100, false, closeDistanceTime = 10, closeDistance = 5)
            val element2 = element.copy(closeDistance = 12)
            val element3 = element.copy(closeDistance = -1)
            val meets = listOf(element, element2, element3)

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect {
                trigger = true
                it shouldHaveSize 1
                it[0].closeDistance shouldBe listOf(element.closeDistance, element2.closeDistance).min()
            }

            trigger shouldBe true
        }
    }

    @Test
    fun `get infected people only infected from meets`() {
        runBlocking {
            //given
            val major = 100
            val minor = 1
            val id = "$major:$minor"
            val id2 = "$major:${minor + 1}"
            val infected = listOf(Infected(id), Infected(id2))
            val element = Meet(100, 1, 1, 100, false, 100, 100)
            val element2 = element.copy(minor = minor + 1)
            val element3 = element.copy(minor = minor + 2)
            val meets = listOf(element, element2, element3)

            whenever(infectedDAO.getInfectedFlow()).thenReturn(flow {
                emit(infected)
            })

            whenever(meetDao.getFlowMeets()).thenReturn(flow {
                emit(meets)
            })
            var trigger = false
            //when then

            infectedRepository.provideInfectedListEvents().collect { meets ->
                trigger = true
                meets shouldHaveSize 2
                meets.any { it.userId == id } shouldBe true
                meets.any { it.userId == id2 } shouldBe true
            }

            trigger shouldBe true
        }
    }
}