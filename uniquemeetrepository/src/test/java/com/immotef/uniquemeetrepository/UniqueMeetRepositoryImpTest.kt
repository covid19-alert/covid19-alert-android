package com.immotef.uniquemeetrepository

import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import com.immotef.testutils.MainCoroutineScopeRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *
 */
internal class UniqueMeetRepositoryImpTest {
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    private lateinit var meetDao: MeetDAO

    private lateinit var repository: com.immotef.uniquemeetrepository.UniqueMeetRepository

    @Before
    fun setUp() {
        meetDao = mock()
        repository = com.immotef.uniquemeetrepository.UniqueMeetRepositoryImp(meetDao)
    }

    private val basicMeet = Meet(1, 2, 1, 2, false, 0, 0)

    @Test
    fun `when 3 the same id from dao only one from repository`() {
        runBlockingTest {
            //given
            val meets =
                listOf(basicMeet.copy(startTime = 3, endTime = 4), basicMeet.copy(startTime = 12, endTime = 15), basicMeet.copy(startTime = 20, endTime = 22))

            whenever(meetDao.getFlowMeets()).thenReturn(flow { emit(meets) })

            val received = mutableListOf<UniqueMeet>()
            //when

            val job = repository.provideUniqueMeets().collect {
                received.addAll(it)
            }

            //then
            received shouldHaveSize 1
            received[0].id shouldBe basicMeet.userId()

        }
    }

    @Test
    fun `when 2 the same id and 1 different id from dao two are returned from repository`() {
        runBlockingTest {
            //given
            val differentIdMeet = basicMeet.copy(major = 20, minor = 22)
            val meets =
                listOf(basicMeet.copy(startTime = 3, endTime = 4), basicMeet.copy(startTime = 12, endTime = 15), differentIdMeet)

            whenever(meetDao.getFlowMeets()).thenReturn(flow { emit(meets) })

            val received = mutableListOf<UniqueMeet>()
            //when

            val job = repository.provideUniqueMeets().collect {
                received.addAll(it)
            }

            //then
            received shouldHaveSize 2
            received[0].id shouldBe basicMeet.userId()
            received[1].id shouldBe differentIdMeet.userId()

        }
    }

}