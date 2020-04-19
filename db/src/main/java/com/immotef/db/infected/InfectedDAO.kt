package com.immotef.db.infected

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.immotef.db.infected_table_name
import kotlinx.coroutines.flow.Flow

/**
 *
 */


@Dao
interface InfectedDAO {
    @Query("SELECT * FROM $infected_table_name")
    suspend fun getInfected(): List<Infected>

    @Query("SELECT * FROM $infected_table_name")
    fun getInfectedFlow(): Flow<List<Infected>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInfected(meets: List<Infected>)
}