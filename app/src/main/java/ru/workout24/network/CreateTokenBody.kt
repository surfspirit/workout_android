package ru.workout24.network

import com.google.gson.annotations.SerializedName

class CreateTokenBody(
    @SerializedName("password")
    var password: String?,
    @SerializedName("email")
    var email: String?
) {
    fun isEmpty(): Boolean = password.isNullOrEmpty() && email.isNullOrEmpty()
}