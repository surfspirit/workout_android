package ru.workout24.ui.exercises_execution

import com.google.gson.annotations.SerializedName

sealed class EndDto(
    @SerializedName("message") val message: String
){
    class SingleExerciseEndDto(
        @SerializedName("id")
        val id: String,
        @SerializedName("single_exercise_id")
        val singleExerciseId: String,
        @SerializedName("count")
        val count: Int,
        @SerializedName("time")
        val time: Int
    ): EndDto("")

    class TrainingEndDto(
        @SerializedName("training_result_id")
        val id: String,
        message: String
    ): EndDto(message)
}

