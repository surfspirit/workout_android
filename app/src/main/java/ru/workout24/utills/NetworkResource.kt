package ru.workout24.utills

import android.annotation.SuppressLint
import androidx.arch.core.util.Function
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import ru.workout24.network.BaseResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Класс для представления ресурса. Чтобы использовать этот класс, от него обязательно нужно отнаследоваться и переопределить абстрактные методы.
 * Чтобы обсервить данные ресурса, необходимо подписаться на свойство resourceLiveData.
 * Если там приходит не null, то последняя загрузка была успешной и можно брать данные
 * иначе можно посмотреть данные соответствующие ошибке в errorData
 * @param ApiDataType тип данных, передаваемых для осуществления запроса за ресурсом, если этот параметр нужен
 * @param DbDataType тип данных успешного ответа
 */
abstract class NetworkResource<ApiDataType, DbDataType, QueryDataType, ErrorDataType : RxGenericErrorConsumer.IError>(
    private val clazz: Class<ErrorDataType>
) {
    // region Private properties
    var isLoading: Boolean = false
        private set
    //лайфдата для основных данных
    var liveData = MutableLiveData<DbDataType?>()
        private set
    //лайфдата для ошибок
    val errorLiveData by lazy {
        RxGenericErrorConsumer(clazz)
    }
    //условие для запроса
    var query: QueryDataType? = null
    var isDirty: Boolean = true
        get() = field || (hasErrors || liveData.value != null)
    //endregion

    // region Private methods
    private fun apiSingle() = getRetrofitCall()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .doOnSuccess { response ->
            isLoading = false
            if (response.isSuccessful) {
                isDirty = false
                response.data?.let { body ->
                    clearCacheInNeeded()
                    saveCallResult(body)
                }
            } else {
                // шлем ошибку в firebase
                Throwable(response.errorMessage).sendFirebaseException()
            }
        }
        .doOnError { throwable ->
            isLoading = false
            errorLiveData.accept(throwable)
            throwable.sendFirebaseException()
        }.flatMap { dbSingle() }

    private fun dbSingle() = getFromDb()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .doOnSuccess {
            liveData.postValue(it)
        }
        .doOnError{throwable ->
            errorLiveData.accept(throwable)
            throwable.sendFirebaseException()
        }

    @SuppressLint("CheckResult")
    private fun loadFromNetwork() {
        isLoading = true
        apiSingle().subscribe({}, {})
    }

    @SuppressLint("CheckResult")
    private fun triggerDataFromCache() {
        if (liveData.value != null) {
            // триггерим обновление данных их кэша оперативной памяти
            liveData.triggerSelf()
        } else { // иначе триггерим обновление данных из персистентного кэша
            dbSingle().subscribe({}, {})
        }
    }
    // endregion

    // region Public methods
    /**
     * Взять данные из кэша, а если их нет или если isDirty, то еще и запросить обновление с сервера
     */
    fun loadIfNeeded() {
        triggerDataFromCache()
        if (isDirty && !isLoading) loadFromNetwork()
    }

    /**
     * Взять данные из кэша и запросить обновление с сервера
     */
    fun load() {
        triggerDataFromCache()
        loadFromNetwork()
    }

    /**
     * Почистить кэш оперативной памяти
     */
    fun clear() {
        errorLiveData.clearErrors()
        liveData.value = null
        liveData.postValue(null)
    }

    /**
     * Функция-расширение, которая упрощает подписку на изменения в ресурсе
     */
    fun onChange(
        lifecycleOwner: LifecycleOwner,
        dataCallback: (DbDataType) -> Unit,
        errorMessageCallback: (String) -> Unit = {}
    ) {
        liveData.observe(lifecycleOwner, Observer {
            it?.let { data -> dataCallback(data) }
        })
        errorLiveData.observeMessage(lifecycleOwner) {
            it?.let { message -> errorMessageCallback(message) }
        }
    }

    fun <MapDataType> onChangeWithMapper(
        lifecycleOwner: LifecycleOwner,
        dataCallback: (MapDataType) -> Unit,
        dataMapper: Function<DbDataType?, MapDataType>,
        errorMessageCallback: (String) -> Unit = {}
    ) {
        Transformations.map(liveData, dataMapper).observe(lifecycleOwner, Observer {
            it?.let { data -> dataCallback(data) }
        })
        errorLiveData.observeMessage(lifecycleOwner) {
            it?.let { message -> errorMessageCallback(message) }
        }
    }
    // endregion

    // region Public properties
    /**
     * Есть ли у ресурса ошибки запроса или иные ошибки
     */
    val hasErrors
        get() = errorLiveData.hasError()

    /**
     * Получить текст ошибки, если он есть
     */
    val errorMessage
        get() = errorLiveData.lastError?.getErrorMessage() ?: errorLiveData.lastThrowable?.message
    // endregion

    protected abstract fun getRetrofitCall(): Single<BaseResponse<ApiDataType>>

    protected abstract fun getFromDb(): Single<DbDataType>

    protected abstract fun saveCallResult(response: ApiDataType)

    protected open fun clearCacheInNeeded() {
    }

    // region Base Classes
    /**
     * Базовые классы
     */
    abstract class WithoutQuery<ApiDataType, DbDataType, ErrorDataType : RxGenericErrorConsumer.IError>(
        private val clazz: Class<ErrorDataType>
    ) : NetworkResource<ApiDataType, DbDataType, Nothing, ErrorDataType>(clazz)

    abstract class WithQueryWithError<ApiDataType, DbDataType, QueryDataType> :
        NetworkResource<ApiDataType, DbDataType, QueryDataType, ConsumerErrorDto>(ConsumerErrorDto::class.java)

    abstract class WithoutQueryWithError<ApiDataType, DbDataType> :
        NetworkResource<ApiDataType, DbDataType, Nothing, ConsumerErrorDto>(ConsumerErrorDto::class.java)

    abstract class WithoutDbSaving<ApiDataType, QueryDataType, ErrorDataType : RxGenericErrorConsumer.IError>(
        private val clazz: Class<ErrorDataType>
    ) : NetworkResource<ApiDataType, ApiDataType, QueryDataType, ErrorDataType>(clazz) {
        override fun saveCallResult(response: ApiDataType) {
            liveData.postValue(response)
        }

        override fun getFromDb(): Single<ApiDataType> =
            Single.fromObservable(Observable.empty<Nothing>())
    }

    abstract class WithoutDbSavingWithQueryWithError<ApiDataType, QueryDataType> :
        WithoutDbSaving<ApiDataType, QueryDataType, ConsumerErrorDto>(ConsumerErrorDto::class.java) {
        override fun saveCallResult(response: ApiDataType) {
            liveData.postValue(response)
        }

        override fun getFromDb(): Single<ApiDataType> =
            Single.fromObservable(Observable.empty<Nothing>())
    }
    // endregion
}
