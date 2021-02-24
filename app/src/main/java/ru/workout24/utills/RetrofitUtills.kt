package ru.workout24.utills

import ru.workout24.network.BaseResponseWithoutData
import com.google.gson.Gson
import retrofit2.Response

fun <T> responseCheck(
    response: Response<T>,
    successCallback: (body: T?) -> Unit = {},
    errorCallback: (errorCode: Int, message: String?) -> Unit = { errorCode, message -> }
) {
    response.errorBody()?.let {
        val gson = Gson()
        val gsonResponse = gson.fromJson(it.string(), BaseResponseWithoutData::class.java)
        errorCallback(gsonResponse?.errorCode ?: 12345, gsonResponse?.errorMessage ?: "Неизвестная ошибка")
    } ?: run {
        successCallback(response.body())
    }
}

fun responsesCheck(
    responses: ArrayList<Response<*>>,
    successCallback: (bodies: ArrayList<Any?>) -> Unit = {},
    errorCallback: (errors: ArrayList<BaseResponseWithoutData>) -> Unit = { }
) {

    val successResponses = arrayListOf<Any?>()
    val errorsResponses = arrayListOf<BaseResponseWithoutData>()

    responses.forEach {
        it.errorBody()?.let {
            val gson = Gson()
            val gsonResponse = gson.fromJson(it.string(), BaseResponseWithoutData::class.java)
            errorsResponses.add(gsonResponse)
        } ?: run {
            successResponses.add(it.body())
        }
    }

    if (successResponses.size != 0) {
        successCallback(successResponses)
    }

    if (errorsResponses.size != 0) {
        errorCallback(errorsResponses)
    }
}

