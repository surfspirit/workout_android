package ru.workout24.ui.auth_layer.test_layer.pojos

import com.google.gson.annotations.SerializedName

data class Exercises(
    @SerializedName("exercises")
    var exercises: List<Exercise?>?
)