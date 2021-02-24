package ru.workout24.utills


import android.accounts.NetworkErrorException
import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class NetworkBoundResource<ResultType, RequestType>(context: Context) {

    private val result: Flowable<Resource<ResultType>>

    init {
        // Lazy disk observable.
        val diskObservable = Flowable.defer {
            loadFromDb()
                // Read from disk on Computation Scheduler
                .subscribeOn(Schedulers.computation())
        }

        // Lazy network observable.
        val networkObservable = Flowable.defer {
            createCall()
                // Request API on IO Scheduler
                .subscribeOn(Schedulers.io())
                // Read/Write to disk on Computation Scheduler
                .observeOn(Schedulers.computation())
                .doOnNext { request: Response<RequestType> ->
                    if (request.isSuccessful) {
                        saveCallResult(processResponse(request))
                    }
                }
                .onErrorReturn { throwable: Throwable ->
                    when (throwable) {
                        is HttpException -> {
                            throw NetworkErrorException()
                        }
                        is IOException -> {
                            throw NetworkErrorException()
                        }
                        else -> {
                            throw NetworkErrorException()
                        }
                    }
                }
                .flatMap { loadFromDb() }
        }

        result = when {
            context.isNetworkStatusAvailable() -> networkObservable
                .map<Resource<ResultType>> { Resource.Success(it) }
                .onErrorReturn { Resource.Failure(it) }
                // Read results in Android Main Thread (UI)
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(Resource.Loading())
            else -> diskObservable
                .map<Resource<ResultType>> { Resource.Success(it) }
                .onErrorReturn { Resource.Failure(it) }
                // Read results in Android Main Thread (UI)
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(Resource.Loading())
        }
    }

    fun asFlowable(): Flowable<Resource<ResultType>> {
        return result
    }

    private fun processResponse(response: Response<RequestType>): RequestType {
        return response.body()!!
    }

    protected abstract fun saveCallResult(request: RequestType)

    protected abstract fun loadFromDb(): Flowable<ResultType>

    protected abstract fun createCall(): Flowable<Response<RequestType>>
}

sealed class Resource<out T> {
    class Loading<out T> : Resource<T>()
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Failure<out T>(val throwable: Throwable) : Resource<T>()

    fun isSuccess(): Boolean = this is Success<T>
    fun isError(): Boolean = this is Failure<T>
    fun isLoading(): Boolean = this is Loading<T>

    fun getSuccessResult(): T? = (this as? Success<T>)?.data
    fun getErrorResult(): Throwable? = (this as? Failure<T>)?.throwable
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