package ru.workout24.utills.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.wtf

abstract class BaseViewModel: ViewModel(), AnkoLogger {

    val loading by lazy {
        MutableLiveData<Boolean>()
    }

    val closeWindow by lazy {
        MutableLiveData<Boolean>()
    }

    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    val error: MutableLiveData<Error?> by lazy {
        MutableLiveData<Error?>()
    }

    fun clearSubscriptions() {
        compositeDisposable.clear()
    }

    open fun sendMessage(uid: Int?, message: String?) {
        error.value = Error(uid, message)
    }

    fun showProgress() {
        loading.value = true
    }

    protected fun hideProgress() {
        loading.value = false
    }

    fun closeWindow(){
        closeWindow.value = true
    }

    override fun onCleared() {
        clearSubscriptions()
        wtf("VIEW MODEL CLEARED")
        super.onCleared()
    }
}

data class Error(
    var uid: Int?,
    var message: String?
)