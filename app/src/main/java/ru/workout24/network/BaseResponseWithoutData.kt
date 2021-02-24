package ru.workout24.network

import com.google.gson.annotations.SerializedName

/**
 * Шаблон ответа сервера
 *
 * @param <T> тип данных в теле ответа
</T> */
class BaseResponseWithoutData {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("code")
    var errorCode: Int? = 0

    @SerializedName("message")
    var errorMessage: String? = null
}