package ru.workout24.ui.auth_layer.test_layer.pojos

import com.google.gson.annotations.SerializedName

data class TestExerciseResultDto(
    @SerializedName("test_exercise_id")
    val test_exercise_id:String?,
    @SerializedName("time")
    val time:Int?,
    @SerializedName("count")
    val count:Int?,
    @SerializedName("id")
    val id:String?
)