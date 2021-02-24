package ru.workout24.ui.auth_layer.register_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.workout24.network.CreateUserBody

object LoginFieldsAutoFill {
    private val loginFieldsData = MutableLiveData<CreateUserBody>()

    fun postLoginData(data: CreateUserBody) {
        loginFieldsData.postValue(data)
    }

    fun getLoginAutoFillFields(): MutableLiveData<CreateUserBody> = loginFieldsData

    fun needScrollToLoginScreen(): LiveData<Boolean> = Transformations.map(loginFieldsData, ::hasFields)

    private fun hasFields(data: CreateUserBody): Boolean =
        data.email?.isNotEmpty() ?: false && data.password?.isNotEmpty() ?: false
}