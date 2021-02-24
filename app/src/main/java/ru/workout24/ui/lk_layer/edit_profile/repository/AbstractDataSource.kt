package ru.workout24.ui.lk_layer.edit_profile.repository

import android.content.Context
import android.net.ConnectivityManager
import ru.workout24.network.BaseResponse
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

abstract class AbstractDataSource<ApiDataType, RoomDataType>(
    private val context: Context,
    private val errorHandler: AbstractErrorHandler? = null
) {
    private var isDirty = false
    private var isLoadingNow = false
    private var isLoadingSuccess = false

    private var result: Flowable<RoomDataType>? = null

    fun load(): Flowable<RoomDataType> {
        val diskObservable = Flowable.defer {
            loadFromDb().subscribeOn(Schedulers.computation())
        }

        // Lazy network observable.
        val networkObservable = Flowable.defer {
            createCall()
                // Request API on IO Scheduler
                .subscribeOn(Schedulers.io())
                // Read/Write to disk on Computation Scheduler
                .observeOn(Schedulers.computation())
                .doOnNext { response: BaseResponse<ApiDataType> ->
                    if (response.isSuccessful) {
                        saveCallResult(response.data!!)
                    } else {
                        errorHandler?.handleBadResponse(response)
                    }
                }
                .flatMap { loadFromDb() }
        }

        result = when {
            context.isNetworkStatusAvailable() -> networkObservable
//                .doOnError {
//                    isLoadingSuccess = false
//                    errorHandler?.handleThrowable(it)
//                }
//                .startWith {
//                    isLoadingNow = true
//                }
//                .doFinally {
//                    isLoadingNow = false
//                }
                .observeOn(AndroidSchedulers.mainThread())
            else -> diskObservable
//                .doOnError {
//                    isLoadingSuccess = false
//                    errorHandler?.handleThrowable(it)
//                }
//                .startWith {
//                    isLoadingNow = true
//                }
//                .doFinally {
//                    isLoadingNow = false
//                }
                .observeOn(AndroidSchedulers.mainThread())
        }

        return result!!
    }

    fun loadIfNeeded(): Flowable<RoomDataType> = if (!isLoadingSuccess || isDirty || !isLoadingNow) {
        load()
    } else {
        result ?: load()
    }

    fun setDirty() {
        isDirty = true
    }

    protected abstract fun saveCallResult(request: ApiDataType)

    protected abstract fun loadFromDb(): Flowable<RoomDataType>

    protected abstract fun createCall(): Flowable<BaseResponse<ApiDataType>>
}

fun Context.isNetworkStatusAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    connectivityManager?.let {
        it.activeNetworkInfo?.let {
            if (it.isConnected) return true
        }
    }
    return false
}

abstract class AbstractErrorHandler {
    abstract fun handleThrowable(throwable: Throwable)
    abstract fun handleBadResponse(response: BaseResponse<*>)
}