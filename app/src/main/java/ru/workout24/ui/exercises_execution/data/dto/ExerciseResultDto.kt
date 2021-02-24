package ru.workout24.ui.exercises_execution.data.dto

import com.google.gson.annotations.SerializedName

sealed class ExerciseResultDto(
    @SerializedName("count")
    val count: Int,
    @SerializedName("time")
    val time: Int,
    open val id: String
) {
    class SingleExerciseDto(
        @SerializedName("single_exercise_id")
        override val id: String,
        count: Int,
        time: Int
    ) : ExerciseResultDto(count, time, id)

    class TrainingExerciseDto(
        @SerializedName("training_exercise_id")
        override val id: String,
        count: Int,
        time: Int
    ) : ExerciseResultDto(count, time, id)

    class TestExerciseDto(
        @SerializedName("test_exercise_id")
        override val id: String,
        count: Int,
        time: Int
    ): ExerciseResultDto(count, time, id)
}