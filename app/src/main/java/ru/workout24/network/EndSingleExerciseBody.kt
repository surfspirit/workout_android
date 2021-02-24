package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class EndSingleExerciseBody(
    @SerializedName("single_exercise_id")
    val singleExerciseId: String?,
    @SerializedName("time")
    val time: Int?,
    @SerializedName("count")
    val count: Int?
)