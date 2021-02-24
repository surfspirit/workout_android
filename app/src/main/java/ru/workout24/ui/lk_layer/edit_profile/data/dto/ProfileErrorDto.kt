package ru.workout24.ui.lk_layer.edit_profile.data.dto


import ru.workout24.utills.RxGenericErrorConsumer
import com.google.gson.annotations.SerializedName

data class ProfileErrorDto(
    @SerializedName("errors")
    val errors: ErrorsDto?,
    @SerializedName("message")
    val message: String?
): RxGenericErrorConsumer.IError{
    override fun getErrorMessage(): String? = message
}