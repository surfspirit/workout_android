package ru.workout24.network

import com.google.gson.annotations.SerializedName

data class CreateUserBody(
    @SerializedName("password")
    var password: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("surname")
    var surname: String?,
    @SerializedName("email")
    var email: String?
) {
    fun getFullName(): String = "$name $surname"
    fun isEmpty(): Boolean = password.isNullOrEmpty() && name.isNullOrEmpty() && surname.isNullOrEmpty() && email.isNullOrEmpty()
}