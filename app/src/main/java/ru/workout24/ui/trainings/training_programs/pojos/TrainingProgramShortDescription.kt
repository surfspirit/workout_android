package ru.workout24.ui.trainings.training_programs.pojos


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity
data class TrainingProgramShortDescription(
    @SerializedName("available")
    val available: Boolean?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("goals")
    val goals: String?,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("preview_url")
    val previewUrl: String?,
    @SerializedName("training_level")
    val trainingLevel: String?,
    @SerializedName("weeks_count")
    val weeks_count: Int?,
    @SerializedName("trainings_total")
    val trainings_total: Int?
)