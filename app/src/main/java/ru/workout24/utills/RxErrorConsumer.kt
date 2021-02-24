package ru.workout24.utills

import androidx.lifecycle.*
import ru.workout24.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import retrofit2.HttpException
import java.lang.Exception

/**
 * Класс одновреенно и обработчик ошибок и [RxSingleLiveData]
 * для обработки ошибок, которые отправляются подписчикам единожды, не сохраняя
 * значение в кэш-памяти
 */
class RxErrorConsumer : RxGenericErrorConsumer<ConsumerErrorDto>(ConsumerErrorDto::class.java)

open class RxGenericErrorConsumer<T : RxGenericErrorConsumer.IError>(private val clazz: Class<T>) : Consumer<Throwable> {
    private val gson by lazy {
        Gson()
    }
    private val observable by lazy {
        RxSingleLiveData<T>()
    }
    private val observableThrowable by lazy {
        RxSingleLiveData<Throwable>()
    }
    //ошибка выполнения api запроса
    var lastError: T? = null
        private set
    //иная ошибка выдаваемая rx
    var lastThrowable: Throwable? = null
        private set

    override fun accept(t: Throwable?) {
        t?.sendFirebaseException()
        if (t is HttpException) {
            val errorJson = t.response()?.errorBody()?.string()
            if (errorJson?.isNotEmpty() == true) {
                try {
                    val error = gson.fromJson<T>(
                        errorJson,
                        clazz
                    )
                    lastError = error
                    observable.postValue(error)
                } catch (e: JsonSyntaxException) {
                    e.logIfDebug()
                    lastThrowable = e
                    observableThrowable.postValue(lastThrowable!!)
                } catch (e: Exception) {
                    e.logIfDebug()
                    lastThrowable = Throwable(message = "Неизвестная ошибка")
                    observableThrowable.postValue(lastThrowable!!)
                }
            }
        } else {
            t?.let { throwable ->
                // если это не ошибка выполнения api запроса, то значит оставляем её как throwable
                throwable.logIfDebug()
                lastThrowable = throwable
                observableThrowable.postValue(throwable)
            }
        }
    }

    fun hasError() = lastError != null || lastThrowable != null

    /**
     * Повторяет отправку последней ошибки подписчику
     */
    fun repeatSendError() {
        lastError?.let { observable.postValue(it) }
        lastThrowable?.let { observableThrowable.postValue(it) }
    }

    fun clearErrors() {
        lastError = null
        lastThrowable = null
    }

    fun observe(lifecycleOwner: LifecycleOwner, o: (T) -> Unit) {
        observable.observe(lifecycleOwner, o)
    }

    fun observeThrowable(lifecycleOwner: LifecycleOwner, o: (Throwable) -> Unit) {
        observableThrowable.observe(lifecycleOwner, o)
    }

    fun observeMessage(lifecycleOwner: LifecycleOwner, o: (String?) -> Unit) {
        observable.observe(lifecycleOwner) {
            o(it.getErrorMessage())
        }
        observableThrowable.observe(lifecycleOwner) {
            o(it.message)
        }
    }

    private fun Throwable.logIfDebug() {
        if (BuildConfig.DEBUG) {
            printStackTrace()
        }
    }

    interface IError {
        fun getErrorMessage(): String?
    }
}

data class ConsumerErrorDto(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: Any?,
    @SerializedName("group")
    val group: Int?,
    @SerializedName("message")
    val message: String?
): RxGenericErrorConsumer.IError {
    override fun getErrorMessage(): String? = message
}

/**
 * Обертка [RxErrorConsumer] для обработки lifecycle
 */
class RxLifecycleHandler<T>(
    owner: LifecycleOwner,
    private val flowable: Flowable<T>,
    private val observer: (T) -> Unit
) : LifecycleObserver {
    private val lifecycle = owner.lifecycle
    private var disposable: Disposable? = null

    init {
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            owner.lifecycle.addObserver(this)
            observeIfPossible()
        }
    }

    private fun observeIfPossible() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            disposable ?: let {
                disposable = flowable.subscribe { data -> observer(data) }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        observeIfPossible()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposable?.dispose()
        disposable = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycle.removeObserver(this)
    }
}