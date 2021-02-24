package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto


import com.google.gson.annotations.SerializedName

data class TrainingExerciseResultDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("time")
    val time: Int?,
    @SerializedName("training_exercise_id")
    val trainingExerciseId: String?
)