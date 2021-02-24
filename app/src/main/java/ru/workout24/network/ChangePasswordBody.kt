package ru.workout24.network


import com.google.gson.annotations.SerializedName

data class ChangePasswordBody(
    @SerializedName("old_password")
    val oldPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)