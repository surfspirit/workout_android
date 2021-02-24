package ru.workout24.ui.trainings.training_programs.pojos


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity
data class TrainingProgramFilter(
    @SerializedName("field")
    val `field`: String?,
    @SerializedName("condition")
    val condition: String?,
    @SerializedName("id")
    @PrimaryKey
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("target")
    val target: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("value")
    val value: String?
)