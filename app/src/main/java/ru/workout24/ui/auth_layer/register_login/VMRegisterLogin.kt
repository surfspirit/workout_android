package ru.workout24.ui.auth_layer.register_login

import ru.workout24.network.*
import ru.workout24.push.data.DeviceIdDto
import ru.workout24.utills.Preferences
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.responseCheck
import ru.workout24.utills.safeLet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.apache.commons.validator.routines.EmailValidator
import org.jetbrains.anko.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VMRegisterLogin @Inject constructor(val api: Api, val pref: Preferences) : BaseViewModel() {

    companion object {
        const val SUCCESS_LOGIN_CODE = 111
        const val SUCCESS_REGISTER_CODE = 555
        const val ERROR_WRONG_EMAIL_CODE = 601
        const val ERROR_WRONG_PASSWORD_CODE = 602
        const val ERROR_EXIST_USER_CODE = 2001
        const val ERROR_SOCIAL = 5001
    }

    val registerBody = CreateUserBody("", "", "", "")
    private val loginBody = CreateTokenBody("", "")
    private val emailValidator: EmailValidator by lazy {
        EmailValidator.getInstance()
    }
    var currentUser: User? = null

    fun setRegisterBody(
        email: CharSequence?,
        pass: CharSequence?,
        name: CharSequence?,
        surname: CharSequence?
    ) {
        email?.let {
            if (it.isNotEmpty() && emailValidator.isValid(it.toString())) {
                registerBody.email = it.toString()
            }
        }
        pass?.let {
            if (it.isNotEmpty()) {
                registerBody.password = it.toString()
            }
        }
        name?.let {
            if (it.isNotEmpty()) {
                registerBody.name = it.toString()
            }
        }
        surname?.let {
            if (it.isNotEmpty()) {
                registerBody.surname = it.toString()
            }
        }
    }

    fun setEmail(email: CharSequence?) {
        email?.let {
            //info { "Email $it" }
            loginBody.email = it.toString()
        }
    }

    fun setPass(pass: CharSequence?) {
        pass?.let {
            //info { "Email $it" }
            loginBody.password = it.toString()
        }
    }

    fun register() {
        if (registerBody.email.isNullOrEmpty()) {
            sendMessage(ERROR_WRONG_EMAIL_CODE, "Некорректно указан логин")
            return
        }
        registerBody.password?.let {
            if (it.length < 8) {
                sendMessage(ERROR_WRONG_PASSWORD_CODE, "Слишком короткий пароль")
                return
            }
        }

        compositeDisposable.add(
            api.createAccount(registerBody)
                .delay(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    responseCheck(response, {
                        it?.data?.let {
                            sendMessage(SUCCESS_REGISTER_CODE, "")
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

    fun login() {
        if (loginBody.email.isNullOrEmpty() || !emailValidator.isValid(loginBody.email)) {
            sendMessage(ERROR_WRONG_EMAIL_CODE, "Некорректно указан логин")
            return
        }
        loginBody.password?.let {
            if (it.length < 8) {
                sendMessage(ERROR_WRONG_PASSWORD_CODE, "Слишком короткий пароль")
                return
            }
        }

        if (!loginBody.isEmpty()) {

            api.createToken(loginBody).enqueue(object : Callback<BaseResponse<TokenResponse>> {
                override fun onFailure(call: Call<BaseResponse<TokenResponse>>, t: Throwable) {
                    sendMessage(-1, "Не удалось получить токен")
                }

                override fun onResponse(
                    call: Call<BaseResponse<TokenResponse>>,
                    response: Response<BaseResponse<TokenResponse>>
                ) {
                    responseCheck(response, {
                        safeLet(it?.data?.token, it?.data?.user) { token, user ->
                            pref.authToken = token
                            if (!pref.pushToken.isNullOrEmpty()) {
                                compositeDisposable.add(
                                    api.registerDevice(DeviceIdDto(pref.pushToken!!)).subscribeOn(
                                        Schedulers.io()
                                    ).subscribe({
                                        it
                                    }, {
                                        it
                                    })
                                )
                            }
                            pref.userProfileId = user.id
                            currentUser = user
                            sendMessage(SUCCESS_LOGIN_CODE, "")
                        } ?: run {
                            sendMessage(-1, "Не удалось получить токен")
                        }
                    }, { errorCode, message ->
                        sendMessage(-1, message)
                    })

                }
            })
        } else {
            sendMessage(-1, "Пожалуста, корректно заполните все поля")
        }
    }

    fun getUser() = compositeDisposable.add(
        api.getUser()
            .delay(600, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ responce ->
                responseCheck(responce, {
                    //TODO("СОХРАНИТЬ ДАННЫЕ ПОЛЬЗОВАТЕЛЯ")
                    sendMessage(SUCCESS_LOGIN_CODE, "")
                }, { errorCode, message ->
                    errorCode
                })

            }, { throwable ->
                error { throwable }
            })
    )
}