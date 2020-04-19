package com.immotef.db.infected

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.immotef.db.infected_table_name

/**
 *
 */


@Entity(
    tableName = infected_table_name, indices = [Index(
        value = ["id"],
        unique = true
    )]
)
data class Infected(
    val id: String,
    @PrimaryKey(autoGenerate = true)
    var infectedId: Int = 0)