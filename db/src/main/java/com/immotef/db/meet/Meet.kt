package com.immotef.db.meet

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.immotef.db.meet_table_name

/**
 *
 */

@Entity(tableName = meet_table_name)
data class Meet(
    val major: Int,
    val minor: Int,
    val startTime: Long,
    val endTime: Long,
    val uploadedToServer: Boolean = false,
    val closeDistance: Int = -1,
    val closeDistanceTime: Long = 0,
    @PrimaryKey(autoGenerate = true)
    var meetId: Int = 0
) {

    fun userId(): String = "$major:$minor"
}