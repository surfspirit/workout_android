package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class ResultTestExerciseBody(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("time")
    val time: Int?,
    @SerializedName("test_exercise_id")
    val testExerciseId: String?
)