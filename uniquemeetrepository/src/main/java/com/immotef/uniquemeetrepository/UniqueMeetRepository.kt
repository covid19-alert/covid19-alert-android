package com.immotef.uniquemeetrepository

import com.immotef.db.meet.MeetDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 *
 */


interface UniqueMeetRepository {
    fun provideUniqueMeets(): Flow<List<UniqueMeet>>
}

internal class UniqueMeetRepositoryImp(private val dao: MeetDAO) : UniqueMeetRepository {
    override fun provideUniqueMeets(): Flow<List<UniqueMeet>> = dao.getFlowMeets().map {
        it.distinctBy { meet -> meet.userId() }.map { inMeet -> UniqueMeet(inMeet.userId()) }
    }.distinctUntilChanged()
}