package com.example.planner.domain.mapper

import com.example.planner.data.database.PlannerActivityEntity
import com.example.planner.domain.model.PlannerActivity

fun PlannerActivityEntity.toDomain(): PlannerActivity =
    PlannerActivity(
        uuid = this.uuid,
        name = this.name,
        datetime = this.datetime,
        isCompleted = this.isCompleted
    )

fun PlannerActivity.toEntity(id: Int): PlannerActivityEntity =
    PlannerActivityEntity(
        id = id,
        uuid = this.uuid,
        name = this.name,
        datetime = this.datetime,
        isCompleted = this.isCompleted
    )