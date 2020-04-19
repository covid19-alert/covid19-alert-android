package com.immotef.db.meet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.immotef.db.meet_table_name
import kotlinx.coroutines.flow.Flow

/**
 *
 */
@Dao
interface MeetDAO {

    @Query("SELECT * FROM $meet_table_name")
    suspend fun getMeets(): List<Meet>

    @Query("SELECT * FROM $meet_table_name")
    fun getFlowMeets(): Flow<List<Meet>>

    @Query("SELECT * FROM $meet_table_name WHERE major = :major AND minor = :minor  ORDER BY endTime DESC LIMIT 1")
    suspend fun getMeetWithSpecificMajorMinor(major: Int, minor: Int): Meet?


    @Query("SELECT * FROM $meet_table_name WHERE uploadedToServer = :uploadedToServer")
    suspend fun getAllMeetsWithSpecificUploadedToServer(uploadedToServer: Boolean = false): List<Meet>

    @Insert
    suspend fun insertMeet(meet: Meet)

    @Insert(onConflict = REPLACE)
    suspend fun updateMeet(meet: Meet)

    @Insert(onConflict = REPLACE)
    suspend fun updateMeets(meets: List<Meet>)

}