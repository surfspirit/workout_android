package ru.workout24.ui.auth_layer.test_layer.pojos

import androidx.room.Embedded
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import com.google.gson.annotations.SerializedName
import ru.workout24.ui.trainings.training_programs.pojos.RequiredRange

data class Exercise(
    @SerializedName("approaches")
    var approaches: Int?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("inventories")
    val inventory: List<Inventories>?,

    @SerializedName("muscle_groups")
    val muscle_groups: List<MuscleGroup>?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("preview_url")
    var previewUrl: String?,

    @SerializedName("required_range")
    @Embedded(prefix = "range")
    val requiredRange: RequiredRange?,
    @SerializedName("required_time")
    var requiredTime: String?,
    @SerializedName("video_url")
    var videoUrl: String?,
    @SerializedName("pause_time")
    var pauseTime: Int?,
    @SerializedName("estimate_time")
    var estimate_time: Int?
)