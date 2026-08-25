package com.example.planner.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerActivityDao {

    @Insert
    fun insert(plannerActivityEntity: PlannerActivityEntity)

    @Query("SELECT * FROM PlannerActivityEntity ORDER BY is_completed AND datetime")
    fun getAll(): Flow<List<PlannerActivityEntity>>

    @Query("SELECT * FROM planneractivityentity WHERE uuid = :uuid")
    fun getByUuid(uuid: String): PlannerActivityEntity

    @Query("UPDATE PlannerActivityEntity SET is_completed = :isCompleted WHERE uuid = :uuid")
    fun updateIsCompletedByUuid(uuid: String, isCompleted: Boolean)

    @Update
    fun update(plannerActivityEntity: PlannerActivityEntity)

    @Query("DELETE FROM PlannerActivityEntity WHERE uuid = :uuid")
    fun deleteByUuid(uuid: String)
}