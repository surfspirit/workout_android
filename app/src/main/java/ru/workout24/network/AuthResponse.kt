package ru.workout24.network

import com.google.gson.annotations.SerializedName

/**
 * Данные о пользователе
 */
class AuthResponse {
    @SerializedName("access_token")
    var access_token: String? = null

    @SerializedName("user_id")
    var user_id: String? = null
}

class RegisterResponse {
    @SerializedName("user_id")
    var user_id: String? = null
}