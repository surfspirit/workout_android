package ru.workout24.ui.workout_diary.data.dto


import com.google.gson.annotations.SerializedName

data class CalendarDto(
    @SerializedName("single_exercises")
    val singleExercises: List<SingleExerciseDto>,
    @SerializedName("trainings")
    val trainings: List<TrainingDto>,
    @SerializedName("training_sets")
    val trainingSets: List<TrainingSetDto>
)