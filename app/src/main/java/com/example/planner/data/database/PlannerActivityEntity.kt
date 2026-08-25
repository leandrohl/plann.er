package com.example.planner.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlannerActivityEntity(
    @PrimaryKey val id: Int,
    val uuid: String,
    val name: String,
    val datetime: Long,
    @ColumnInfo("is_completed") val isCompleted: Boolean
)