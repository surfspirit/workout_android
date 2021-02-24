package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto


import com.google.gson.annotations.SerializedName

data class TrainingResultMetaDto(
    @SerializedName("assignment_id")
    var assignmentId: String,
    @SerializedName("difficulty")
    var difficulty: DIFFICULTY,
    @SerializedName("id")
    var id: String,
    @SerializedName("like")
    var like: Boolean,
    @SerializedName("photos_url")
    var photosUrl: List<String>?,
    @SerializedName("training_id")
    var trainingId: String
)

enum class DIFFICULTY(val value: String) {
    VERY_SIMPLE("Очень легко"),
    SIMPLE("Легко"),
    NORMAL("Нормально"),
    HARD("Сложно"),
    VERY_HARD("Очень сложно")
}