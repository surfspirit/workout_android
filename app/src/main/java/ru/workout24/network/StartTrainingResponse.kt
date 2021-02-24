package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class StartTrainingResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("training_assignment_id")
    val trainingAssignmentId: String
)