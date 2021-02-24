package ru.workout24.network

import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import com.google.gson.annotations.SerializedName

data class MuscleGroupResponse(
    @SerializedName("muscle-groups")
    val muscle_groups:List<MuscleGroup>
)