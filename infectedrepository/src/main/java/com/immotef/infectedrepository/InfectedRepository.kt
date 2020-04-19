package com.immotef.infectedrepository

import com.immotef.db.infected.Infected
import com.immotef.db.infected.InfectedDAO
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Collections.max
import java.util.Collections.min

/**
 *
 */
data class InfectedMeet(
    val userId: String,
    val startTime: Long,
    val endTime: Long,
    val closeDistance: Int = -1,
    val closeDistanceTime: Long = 0)

interface InfectedRepository {
    suspend fun saveInfected(infectedIds: List<String>?)
    fun provideInfectedListEvents(): Flow<List<InfectedMeet>>
}

internal class InfectedRepositoryImp(
    private val infectedDAO: InfectedDAO,
    private val meetDAO: MeetDAO
) : InfectedRepository {
    override suspend fun saveInfected(infectedIds: List<String>?) {
        infectedIds?.takeIf { it.isNotEmpty() }
            ?.apply { infectedDAO.updateInfected(this.map { Infected(it) }) }
    }

    override fun provideInfectedListEvents(): Flow<List<InfectedMeet>> {
        return infectedDAO.getInfectedFlow().combine(meetDAO.getFlowMeets()) { infected, meets ->
            infected
                .filter { inf -> anyMeetHasThisId(meets, inf) }
                .map { inf ->
                    meets
                        .filter { it.userId() == inf.id }
                        .reduce { acc: Meet, added ->
                            val closeDistance = if (added.closeDistance == -1) 100 else added.closeDistance
                            acc.copy(
                                closeDistance = min(listOf(acc.closeDistance, closeDistance)),
                                closeDistanceTime = acc.closeDistanceTime + added.closeDistanceTime,
                                endTime = max(listOf(acc.endTime, added.endTime))
                            )
                        }
                }.map {
                    InfectedMeet(it.userId(), it.startTime, it.endTime, it.closeDistance, it.closeDistanceTime)
                }
        }
    }

    private fun anyMeetHasThisId(meets: List<Meet>, inf: Infected) =
        meets.any { meet -> meet.userId() == inf.id }
}