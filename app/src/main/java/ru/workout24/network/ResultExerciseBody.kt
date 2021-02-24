package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class ResultExerciseBody(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("time")
    val time: Int?,
    @SerializedName("training_exercise_id")
    val trainingExerciseId: String?
)