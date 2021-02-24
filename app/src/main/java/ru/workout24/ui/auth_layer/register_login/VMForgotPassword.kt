package ru.workout24.ui.auth_layer.register_login

import androidx.lifecycle.MutableLiveData
import ru.workout24.network.Api
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.responseCheck
import com.google.gson.annotations.SerializedName
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.validator.routines.EmailValidator
import org.jetbrains.anko.info
import javax.inject.Inject

class VMForgotPassword @Inject constructor(val api: Api) : BaseViewModel() {

    companion object {
        const val ERROR_WRONG_EMAIL_CODE = 601
        const val SUCCESS_CODE = 200
    }

    data class restoreBody(
        @SerializedName("email")
        var email: String?
    )

    val email = MutableLiveData<String>()
    private val emailValidator: EmailValidator by lazy {
        EmailValidator.getInstance()
    }

    fun sendEmail() {
        email.value?.let {
            info { "Email $it" }
            if (it.isNotEmpty()) {
                if (emailValidator.isValid(it.toString())) {
                    sendRequest()
                } else {
                    sendMessage(ERROR_WRONG_EMAIL_CODE, "")
                }
            }
        }
    }

    private fun sendRequest() {
        compositeDisposable.add(
            api.restorePassword(restoreBody(email.value.toString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    responseCheck(response, {
                        it?.data?.let {
                            sendMessage(SUCCESS_CODE, "")
                        } ?: run {
                            sendMessage(-1, "Произошла ошибка связи с сервером")
                        }
                    }, { errorCode, message ->
                        sendMessage(errorCode, message)
                    })

                }, { throwable ->
                    error { throwable }
                })
        )
    }
}